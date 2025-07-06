package io.petprojects.bookshelfs.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReaderInfoResponse {
    private String publicName;
    private String bookCount;
    private List<BookshelfListResponse> bookshelfs;
}
