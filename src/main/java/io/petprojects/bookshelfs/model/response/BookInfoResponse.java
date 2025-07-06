package io.petprojects.bookshelfs.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookInfoResponse {
    private String title;
    private String contentType;
    private String description;
    private boolean isGivenToRead;
    private boolean isEternalAccess;
    private LocalDateTime bookReturnDeadLine;
    private String bookOwner;
    private String bookshelf;
}
