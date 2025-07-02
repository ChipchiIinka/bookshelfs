package io.petprojects.bookshelfs.exception.baseresponse;

import io.petprojects.bookshelfs.exception.BookshelfsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseResponseService {

    private final MessageSource messageSource;

    public <T> ResponseWrapper<T> wrapSuccessResponse(T body){
        return ResponseWrapper
                .<T>builder()
                .success(true)
                .body(body)
                .build();
    }

    public ResponseWrapper<?> wrapErrorResponse (BookshelfsException exception, String logMessage, Locale locale) {
        log.error(logMessage);
        String localizedMessage = exception.getMessage() != null ? exception.getMessage() :
                messageSource.getMessage(exception.getType().getMessageKey(), null, locale);
        ErrorDto error = ErrorDto.builder()
                .code(exception.getType().name())
                .message(localizedMessage)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseWrapper
                .builder()
                .success(false)
                .error(error)
                .build();
    }
}
