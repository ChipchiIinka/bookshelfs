package io.petprojects.bookshelfs.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReaderListResponse {
    private Long id;
    private String publicName;
    private Integer bookCount;
}
