package io.petprojects.bookshelfs.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookReadResponse {
    private byte[] data;
}
