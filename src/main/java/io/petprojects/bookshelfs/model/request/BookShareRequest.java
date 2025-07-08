package io.petprojects.bookshelfs.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookShareRequest {
    @Schema(example = "Одолжи книгу")
    private String comment;

    @Schema(example = "TEMPORARY")
    private String accessType;

    @Schema(type = "string", format = "date", example = "2025-12-31")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
}
