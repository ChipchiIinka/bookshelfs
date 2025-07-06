package io.petprojects.bookshelfs.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookListResponse {
    private Long id;
    private String title;
    private String bookOwner;
}
