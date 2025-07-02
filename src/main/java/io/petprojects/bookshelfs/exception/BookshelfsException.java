package io.petprojects.bookshelfs.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookshelfsException extends RuntimeException {

    private final ErrorType type;

    public BookshelfsException(ErrorType type, String massage){
        super(massage);
        this.type = type;
    }

    public BookshelfsException(ErrorType type, Throwable throwable){
        super(throwable);
        this.type = type;
    }

    public BookshelfsException(ErrorType type, String massage, Throwable throwable){
        super(massage, throwable);
        this.type = type;
    }
}
