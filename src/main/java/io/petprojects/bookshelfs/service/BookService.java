package io.petprojects.bookshelfs.service;

import io.petprojects.bookshelfs.entity.BookEntity;
import io.petprojects.bookshelfs.entity.BookshelfEntity;
import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.exception.BookshelfsException;
import io.petprojects.bookshelfs.exception.ErrorType;
import io.petprojects.bookshelfs.model.request.BookUpdateRequest;
import io.petprojects.bookshelfs.model.response.BookInfoResponse;
import io.petprojects.bookshelfs.model.response.BookListResponse;
import io.petprojects.bookshelfs.model.response.BookReadResponse;
import io.petprojects.bookshelfs.repository.BookRepository;
import io.petprojects.bookshelfs.repository.BookshelfRepository;
import io.petprojects.bookshelfs.repository.ReaderRepository;
import io.petprojects.bookshelfs.service.mapper.BookMapper;
import io.petprojects.bookshelfs.utill.MimeTypeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private static final Integer BOOKSHELF_MAX_CAPACITY = 10;

    private final ReaderRepository readerRepository;
    private final BookshelfRepository bookshelfRepository;
    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public List<BookListResponse> findAll(Long bookshelfId) {
        BookshelfEntity bookshelf = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Полка не найдена с id: " + bookshelfId));
        return bookMapper.toListResponse(bookshelf.getBooks());
    }

    public BookInfoResponse findById(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Книга не найдена с id: " + bookId));
        return bookMapper.toInfoResponse(book);
    }

    public BookReadResponse readBook(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Книга не найдена с id: " + bookId));
        return bookMapper.toReadResponse(book);
    }

    public String create(Long readerId, Long bookshelfId,
                         MultipartFile multipartFile, String description) throws IOException {
        ReaderEntity reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Пользователь не найден с id: " + readerId));
        BookshelfEntity bookshelf = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Полка не найдена с id: " + bookshelfId));
        if (bookshelf.getBookshelfCapacity() >= BOOKSHELF_MAX_CAPACITY) {
            throw new BookshelfsException(ErrorType.CLIENT_ERROR, "На этой полке больше нет места.");
        }
        BookEntity book = bookMapper.toEntity(multipartFile);
        String mimeType = multipartFile.getContentType();
        String readableMimeType = MimeTypeUtil.getReadableMimeType(mimeType);
        book.setContentType(readableMimeType);
        book.setDescription(description);
        book.setBookOwner(reader);
        book.setBookshelf(bookshelf);
        bookRepository.save(book);
        bookshelf.setBookshelfCapacity(bookshelf.getBookshelfCapacity() + 1);
        bookshelfRepository.save(bookshelf);
        reader.setBookCount(reader.getBookCount() + 1);
        readerRepository.save(reader);
        return "Книга успешно добавлена";
    }

    public String update(Long bookId, BookUpdateRequest bookUpdateRequest) {
        BookEntity book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                                        "Книга не найдена с id: " + bookId));
        book.setTitle(bookUpdateRequest.getTitle());
        book.setDescription(bookUpdateRequest.getDescription());
        bookRepository.save(book);
        return "Информация о книге успешно обновлена";
    }

    @Transactional
    public String delete(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Книга не найдена с id: " + bookId));
        ReaderEntity reader = book.getBookOwner();
        reader.setBookCount(reader.getBookCount() - 1);
        BookshelfEntity bookshelf = book.getBookshelf();
        bookshelf.setBookshelfCapacity(bookshelf.getBookshelfCapacity() - 1);
        readerRepository.save(reader);
        bookshelfRepository.save(bookshelf);
        bookRepository.delete(book);
        return "Книга успешно удалена";
    }
}
