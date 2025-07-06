package io.petprojects.bookshelfs.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookUpdateRequest {
    private String title;
    private String description;
}
