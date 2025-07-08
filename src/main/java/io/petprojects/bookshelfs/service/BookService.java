package io.petprojects.bookshelfs.service;

import io.petprojects.bookshelfs.entity.BookEntity;
import io.petprojects.bookshelfs.entity.BookshelfEntity;
import io.petprojects.bookshelfs.entity.ReaderBookProgressEntity;
import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.entity.enums.BookStatus;
import io.petprojects.bookshelfs.exception.BookshelfsException;
import io.petprojects.bookshelfs.exception.ErrorType;
import io.petprojects.bookshelfs.model.request.BookUpdateRequest;
import io.petprojects.bookshelfs.model.response.BookContentResponse;
import io.petprojects.bookshelfs.model.response.BookInfoResponse;
import io.petprojects.bookshelfs.model.response.BookListResponse;
import io.petprojects.bookshelfs.repository.BookRepository;
import io.petprojects.bookshelfs.repository.BookshelfRepository;
import io.petprojects.bookshelfs.repository.ReaderBookProgressRepository;
import io.petprojects.bookshelfs.repository.ReaderRepository;
import io.petprojects.bookshelfs.service.mapper.BookMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final ReaderRepository readerRepository;
    private final BookshelfRepository bookshelfRepository;
    private final BookRepository bookRepository;
    private final ReaderBookProgressRepository readerBookProgressRepo;
    private final BookShareService bookShareService;
    private final BookMapper bookMapper;

    private static final Integer BOOKSHELF_MAX_CAPACITY = 10;


    public List<BookListResponse> findAll(Long bookshelfId) {
        BookshelfEntity bookshelf = getBookshelfById(bookshelfId);
        return bookMapper.toListResponse(bookshelf.getBooks());
    }

    public BookInfoResponse findById(Long bookId) {
        BookEntity book = getBookById(bookId);
        return bookMapper.toInfoResponse(book);
    }

    public String upload(Long readerId, Long bookshelfId,
                         MultipartFile file, String description) throws IOException {
        ReaderEntity reader = getReaderById(readerId);
        BookshelfEntity bookshelf = getBookshelfById(bookshelfId);
        if (bookshelf.getBookshelfCapacity() >= BOOKSHELF_MAX_CAPACITY) {
            throw new BookshelfsException(ErrorType.CLIENT_ERROR,
                    "На этой полке больше нет места.");
        }
        BookEntity book = new BookEntity();
        book.setTitle(file.getOriginalFilename());
        book.setDescription(description);
        book.setBookOwner(reader);
        book.setBookshelf(bookshelf);
        book.setStatus(BookStatus.PENDING);
        book.setFilePath(saveFile(file));
        book = bookRepository.save(book);

        convertBookToHtml(book.getId(), book.getFilePath(), readerId, bookshelfId);

        bookshelf.setBookshelfCapacity(bookshelf.getBookshelfCapacity() + 1);
        bookshelfRepository.save(bookshelf);
        reader.setBookCount(reader.getBookCount() + 1);
        readerRepository.save(reader);
        return "Книга успешно добавлена";
    }

    public String update(Long bookId, BookUpdateRequest bookUpdateRequest) {
        BookEntity book = getBookById(bookId);
        book.setTitle(bookUpdateRequest.getTitle());
        book.setDescription(bookUpdateRequest.getDescription());
        book.setBookshelf(getBookshelfById(bookUpdateRequest.getNewBookshelfId()));
        bookRepository.save(book);
        return "Информация о книге успешно обновлена";
    }

    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        Files.createDirectories(Path.of(uploadDir));
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Path.of(uploadDir, fileName);
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return filePath.toString();
    }

    @Async("taskExecutor")
    public void convertBookToHtml(Long bookId, String filePath, Long readerId, Long bookshelfId) {
        try {
            BookEntity book = bookRepository.findById(bookId).orElseThrow();
            File file = new File(filePath);
            PDDocument document = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            String htmlContent = """
                <div class='book-content' id='book-content'>
                    %s
                </div>
                <script>
                    document.addEventListener('DOMContentLoaded', () => {
                        const lastPage = localStorage.getItem('lastPage_%s') || 0;
                        window.scrollTo(0, lastPage * window.innerHeight);
                        window.addEventListener('scroll', () => {
                            const page = Math.floor(window.scrollY / window.innerHeight);
                            localStorage.setItem('lastPage_%s', page);
                            fetch('http://localhost:8080/api/readers/%s/bookshelfs/%s/books/%s/progress', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ lastPage: page })
                            });
                        });
                    });
                </script>
                """.formatted(
                    text.replace("\n", "<br>")
                            .replace("\r", "")
                            .replace("  ", "  "),
                    bookId, bookId, readerId, bookshelfId, bookId
            );
            String htmlPath = filePath.replace(".pdf", ".html");
            Files.write(Path.of(htmlPath), htmlContent.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            book.setStatus(BookStatus.CONVERTED);
            book.setHtmlPath(htmlPath);
            bookRepository.save(book);
        } catch (IOException e) {
            BookEntity book = bookRepository.findById(bookId).orElseThrow();
            book.setStatus(BookStatus.FAILED);
            bookRepository.save(book);
            throw new BookshelfsException(ErrorType.CLIENT_ERROR, "Не удалось преобразовать книгу", e);
        }
    }

    public BookContentResponse readBook(Long bookId, Long readerId) {
        BookEntity book = getBookById(bookId);
        ReaderBookProgressEntity progress = readerBookProgressRepo.findByReaderIdAndBookId(readerId, bookId)
                .orElse(createReadBookProgress(readerId, bookId));
        if (!book.getStatus().equals(BookStatus.CONVERTED)) {
            return bookMapper.toContentResponse("Книга ещё не готова для чтения",  0);
        }
        if (!readerId.equals(book.getBookOwner().getId()) && !bookShareService.hasAccess(readerId, bookId)) {
            bookshelfRepository.delete(book.getBookshelf());
            return bookMapper.toContentResponse("Доступ запрещен", 0);
        }
        try {
            String content = Files.readString(Path.of(book.getHtmlPath()));
            return bookMapper.toContentResponse(content, progress.getLastReadPage());
        } catch (IOException e) {
            throw new BookshelfsException(ErrorType.COMMON_ERROR, "Попытка чтения закончилась ошибкой", e);
        }
    }

    public String updateProgress(Long readerId, Long bookId, int lastReadPage) {
        ReaderBookProgressEntity readerBookProgress = readerBookProgressRepo.findByReaderIdAndBookId(readerId, bookId)
                .orElse(createReadBookProgress(readerId, bookId));
        readerBookProgress.setLastReadPage(lastReadPage);
        readerBookProgressRepo.save(readerBookProgress);
        return "Прогресс чтения обновлен";
    }

    private ReaderBookProgressEntity createReadBookProgress(Long readerId, Long bookId) {
        ReaderBookProgressEntity readerBookProgress = new ReaderBookProgressEntity();
        readerBookProgress.setReader(getReaderById(readerId));
        readerBookProgress.setBook(getBookById(bookId));
        readerBookProgress.setLastReadPage(0);
        readerBookProgressRepo.save(readerBookProgress);
        return readerBookProgress;
    }

    @Transactional
    public String delete(Long bookId) {
        BookEntity book = getBookById(bookId);
        ReaderEntity reader = book.getBookOwner();
        reader.setBookCount(reader.getBookCount() - 1);
        BookshelfEntity bookshelf = book.getBookshelf();
        bookshelf.setBookshelfCapacity(bookshelf.getBookshelfCapacity() - 1);
        readerRepository.save(reader);
        bookshelfRepository.save(bookshelf);
        deleteBookFiles(book);
        bookRepository.delete(book);
        return "Книга успешно удалена";
    }

    private void deleteBookFiles(BookEntity book) {
        try {
            if (book.getFilePath() != null && !book.getFilePath().isEmpty()) {
                Path filePath = Path.of(book.getFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }
            if (book.getHtmlPath() != null && !book.getHtmlPath().isEmpty()) {
                Path htmlPath = Path.of(book.getHtmlPath());
                if (Files.exists(htmlPath)) {
                    Files.delete(htmlPath);
                }
            }
        } catch (IOException e) {
            throw new BookshelfsException(ErrorType.COMMON_ERROR, "Ошибка удаления книги", e);
        }

    }

    private BookEntity getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Книга не найдена с id: " + bookId));
    }

    private BookshelfEntity getBookshelfById (Long bookshelfId) {
        return bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Полка не найдена с id: " + bookshelfId));
    }

    private ReaderEntity getReaderById (Long readerId) {
        return readerRepository.findById(readerId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Читатель не найдена с id: " + readerId));
    }
}
