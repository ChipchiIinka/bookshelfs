package io.petprojects.bookshelfs.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookContentResponse {
    private String content;
    private int lastPage;
}
