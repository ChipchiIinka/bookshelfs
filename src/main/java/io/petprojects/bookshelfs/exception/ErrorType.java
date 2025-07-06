package io.petprojects.bookshelfs.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    COMMON_ERROR("error.common_error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("error.not_found", HttpStatus.NOT_FOUND),
    CLIENT_ERROR("error.client_error", HttpStatus.BAD_REQUEST),
    FORBIDDEN("error.forbidden", HttpStatus.FORBIDDEN),;

    private final String messageKey;
    private final HttpStatus httpStatus;
}
