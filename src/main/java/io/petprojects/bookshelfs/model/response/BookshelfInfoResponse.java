package io.petprojects.bookshelfs.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookshelfInfoResponse {
    private String title;
    private List<BookListResponse> booksResponse;
}
