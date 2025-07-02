package io.petprojects.bookshelfs.exception;

import io.petprojects.bookshelfs.exception.baseresponse.BaseResponseService;
import io.petprojects.bookshelfs.exception.baseresponse.ResponseWrapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionApiHandler {

    private final BaseResponseService baseResponseService;

    @ExceptionHandler(Throwable.class)
    public ResponseWrapper<?> handleOtherException(Throwable t, Locale locale) {
        log.error("Unexpected error: {}", t.getMessage(), t);
        return baseResponseService.wrapErrorResponse(
                new BookshelfsException(ErrorType.COMMON_ERROR, t),
                String.format("Unexpected error: %s", t.getMessage()),
                locale
        );
    }

    @ExceptionHandler(BookshelfsException.class)
    public ResponseWrapper<?> handleBookshelfsException(BookshelfsException exception, Locale locale) {
        return baseResponseService.wrapErrorResponse(
                exception,
                String.format("BookshelfsException: %s, message: %s",
                        exception.getType().name(), exception.getMessage()),
                locale
        );
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseWrapper<?> handlePropertyReferenceException(PropertyReferenceException exception,
                                                                     Locale locale) {
        return baseResponseService.wrapErrorResponse(
                new BookshelfsException(ErrorType.CLIENT_ERROR, exception),
                String.format("PropertyReferenceException: %s", exception.getMessage()),
                locale
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseWrapper<?> handleDataIntegrityViolationException(DataIntegrityViolationException exception,
                                                                          Locale locale) {
        return baseResponseService.wrapErrorResponse(
                new BookshelfsException(ErrorType.CLIENT_ERROR,
                        "Проверьте уникальность данных (например, email или username)", exception),
                String.format("DataIntegrityViolationException: %s", exception.getMessage()),
                locale
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseWrapper<?> handleValidationException(MethodArgumentNotValidException exception,
                                                              Locale locale) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));
        return baseResponseService.wrapErrorResponse(
                new BookshelfsException(ErrorType.CLIENT_ERROR, message),
                String.format("Validation error: %s", message),
                locale
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseWrapper<?> handleConstraintViolationException(ConstraintViolationException exception, Locale locale) {
        String message = exception.getConstraintViolations()
                .stream()
                .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining("; "));
        return baseResponseService.wrapErrorResponse(
                new BookshelfsException(ErrorType.CLIENT_ERROR, message),
                String.format("Constraint violation: %s", message),
                locale
        );
    }
}