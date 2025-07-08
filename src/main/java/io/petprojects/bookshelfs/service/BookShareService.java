package io.petprojects.bookshelfs.service;

import io.petprojects.bookshelfs.entity.BookEntity;
import io.petprojects.bookshelfs.entity.BookShareEntity;
import io.petprojects.bookshelfs.entity.BookshelfEntity;
import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.entity.enums.AccessType;
import io.petprojects.bookshelfs.entity.enums.BookShareStatus;
import io.petprojects.bookshelfs.exception.BookshelfsException;
import io.petprojects.bookshelfs.exception.ErrorType;
import io.petprojects.bookshelfs.model.request.BookShareRequest;
import io.petprojects.bookshelfs.model.response.BookShareNotificationResponse;
import io.petprojects.bookshelfs.repository.BookRepository;
import io.petprojects.bookshelfs.repository.BookShareRepository;
import io.petprojects.bookshelfs.repository.BookshelfRepository;
import io.petprojects.bookshelfs.repository.ReaderRepository;
import io.petprojects.bookshelfs.service.mapper.BookShareMapper;
import io.petprojects.bookshelfs.service.secure.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookShareService {
    private final BookShareRepository bookShareRepository;
    private final BookShareMapper bookShareMapper;
    private final EmailService emailService;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    private static final String BOOK_REQUEST_SUBJECT = "Входящий запрос на одолжение книги";
    private static final String BOOK_REQUEST_TEXT = "Уважаемый, %s, читатель %s хочет почитать вашу книгу %s. Комментарий от читателя: %s";
    private static final String BOOK_RESPONSE_SUBJECT = "Ответ на запрос об одолжении книги";
    private static final String BOOK_GOOD_RESPONSE_TEXT = "Владелец книги %s, %s одолжил вам книгу %s.";
    private static final String BOOK_BAD_RESPONSE_TEXT = "Владелец книги решил НЕ давать вам книгу. Причина: %s";
    private final BookshelfRepository bookshelfRepository;

    public List<BookShareNotificationResponse> getAll(Long readerId) {
        return bookShareMapper.toListInfoResponse(bookShareRepository.findAllByReaderId(readerId));
    }

    public String createShareRequest(Long bookId, Long requesterId, BookShareRequest request) {
        ReaderEntity requester = getReaderById(requesterId);
        BookEntity book = getBookById(bookId);
        ReaderEntity owner = book.getBookOwner();
        BookShareEntity bookShare = new BookShareEntity();
        bookShare.setBook(book);
        bookShare.setRequester(requester);
        bookShare.setOwner(owner);
        bookShare.setComment(request.getComment());
        bookShare.setShareStatus(BookShareStatus.PENDING);
        bookShare.setAccessType(AccessType.valueOf(request.getAccessType().toUpperCase()));
        if (AccessType.TEMPORARY.equals(AccessType.valueOf(request.getAccessType().toUpperCase()))) {
            bookShare.setExpirationDate(request.getExpirationDate());
        }
        emailService.sendNotifyEmail(owner.getEmail(), BOOK_REQUEST_SUBJECT, String.format(
                BOOK_REQUEST_TEXT, owner.getPublicName(), requester.getPublicName(),
                book.getTitle(), request.getComment()));
        bookShareRepository.save(bookShare);
        return "Запрос успешно отправлен";
    }

    @Transactional
    public String handleShareRequest(Long notificationId, String status, String rejectionReason) {
        BookShareEntity request = getBookShare(notificationId);
        String bookOwnerName = request.getOwner().getPublicName();
        String requesterEmail = request.getRequester().getEmail();
        String bookTitle = request.getBook().getTitle();
        request.setShareStatus(BookShareStatus.valueOf(status.toUpperCase()));
        if (BookShareStatus.REJECTED.equals(BookShareStatus.valueOf(status.toUpperCase()))) {
            request.setRejectionReason(rejectionReason);
            emailService.sendNotifyEmail(requesterEmail, BOOK_RESPONSE_SUBJECT,
                    String.format(BOOK_BAD_RESPONSE_TEXT, rejectionReason));
            bookShareRepository.save(request);
            return "Отрицательный ответ успешно отправлен";
        }
        emailService.sendNotifyEmail(requesterEmail, BOOK_RESPONSE_SUBJECT, String.format(
                BOOK_GOOD_RESPONSE_TEXT, bookOwnerName, request.getShareStatus().name(), bookTitle));
        BookEntity bookCopy = createBookCopyForRequester(request.getBook(), request.getRequester());
        request.setBook(bookCopy);
        bookShareRepository.save(request);
        return "Положительный ответ успешно отправлен, книга одолжена";
    }

    public boolean hasAccess(Long userId, Long bookId) {
        return bookShareRepository.findByBookIdAndRequesterIdAndShareStatus(bookId, userId, BookShareStatus.APPROVED)
                .map(request -> AccessType.PERMANENT.equals(request.getAccessType()) ||
                        (AccessType.TEMPORARY.equals(request.getAccessType()) &&
                                request.getExpirationDate().isBefore(LocalDate.now())))
                .orElse(false);
    }

    private BookShareEntity getBookShare(Long id) {
        return bookShareRepository.findById(id)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND, "Запрос не найден"));
    }

    private ReaderEntity getReaderById (Long readerId) {
        return readerRepository.findById(readerId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Читатель не найдена с id: " + readerId));
    }

    private BookEntity getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Книга не найдена с id: " + bookId));
    }

    private BookEntity createBookCopyForRequester(BookEntity book, ReaderEntity requester) {
        BookEntity bookCopy = new BookEntity();
        bookCopy.setTitle(book.getTitle());
        bookCopy.setBookOwner(book.getBookOwner());
        bookCopy.setDescription(book.getDescription());
        bookCopy.setHtmlPath(book.getHtmlPath());
        bookCopy.setStatus(book.getStatus());
        bookCopy.setFilePath(book.getFilePath());

        BookshelfEntity bookshelf = new BookshelfEntity();
        bookshelf.setTitle("Одолженная книга - " + book.getTitle());
        bookshelf.setBookshelfCapacity(10);
        bookshelf.setBooks(List.of(bookCopy));
        bookshelf.setReader(requester);
        bookshelfRepository.save(bookshelf);

        bookCopy.setBookshelf(bookshelf);
        return bookRepository.save(bookCopy);
    }
}
