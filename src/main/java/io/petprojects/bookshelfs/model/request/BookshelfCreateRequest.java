package io.petprojects.bookshelfs.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookshelfCreateRequest {
    @NotNull
    @Size(min = 1, max = 20)
    private String title;
}
