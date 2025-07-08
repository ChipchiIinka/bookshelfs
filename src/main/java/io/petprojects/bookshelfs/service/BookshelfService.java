package io.petprojects.bookshelfs.service;

import io.petprojects.bookshelfs.entity.BookshelfEntity;
import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.exception.BookshelfsException;
import io.petprojects.bookshelfs.exception.ErrorType;
import io.petprojects.bookshelfs.model.response.BookshelfInfoResponse;
import io.petprojects.bookshelfs.model.response.BookshelfListResponse;
import io.petprojects.bookshelfs.repository.BookshelfRepository;
import io.petprojects.bookshelfs.repository.ReaderRepository;
import io.petprojects.bookshelfs.service.mapper.BookshelfMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookshelfService {
    private final BookshelfRepository bookshelfRepository;
    private final BookService bookService;
    private final BookshelfMapper bookshelfMapper;
    private final ReaderRepository readerRepository;

    public List<BookshelfListResponse> findAll(Long readerId) {

        ReaderEntity readerEntity = getReaderById(readerId);
        List<BookshelfEntity> bookshelfEntities = readerEntity.getBookshelfs();
        return bookshelfMapper.toListResponse(bookshelfEntities);
    }

    public BookshelfInfoResponse findById(Long bookshelfId) {
        BookshelfEntity bookshelfEntity = getBookshelfById(bookshelfId);
        return bookshelfMapper.toInfoResponse(bookshelfEntity, bookService.findAll(bookshelfId));
    }

    public String create(Long readerId, String bookshelfTitle) {
        ReaderEntity readerEntity = getReaderById(readerId);
        BookshelfEntity bookshelfEntity = new BookshelfEntity();
        bookshelfEntity.setTitle(bookshelfTitle);
        bookshelfEntity.setReader(readerEntity);
        bookshelfEntity.setBookshelfCapacity(0);
        bookshelfRepository.save(bookshelfEntity);
        return "Книжная полка успешно создана";
    }

    public String updateTitle(Long bookshelfId, String bookshelfNewTitle) {
        BookshelfEntity bookshelf = getBookshelfById(bookshelfId);
        bookshelf.setTitle(bookshelfNewTitle);
        bookshelfRepository.save(bookshelf);
        return "Название полки успешно изменено";
    }

    @Transactional
    public String delete(Long bookshelfId) {
        BookshelfEntity bookshelf = getBookshelfById(bookshelfId);
        ReaderEntity reader = bookshelf.getReader();
        reader.setBookCount(reader.getBookCount() - bookshelf.getBooks().size());
        readerRepository.save(reader);
        bookshelfRepository.delete(bookshelf);
        return "Полка успешно удалена";
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
