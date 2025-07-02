package io.petprojects.bookshelfs.exception.baseresponse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorDto {
    private String code;
    private String message;
    private LocalDateTime timestamp;
}


