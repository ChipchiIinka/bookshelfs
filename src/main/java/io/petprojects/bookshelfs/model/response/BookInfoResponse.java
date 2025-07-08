package io.petprojects.bookshelfs.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookInfoResponse {
    private String title;
    private String description;
    private String bookOwner;
    private String bookshelf;
}
