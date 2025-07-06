package io.petprojects.bookshelfs.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {

    @NotNull
    @Schema(example = "testuser")
    private String login;

    @NotNull
    @Schema(example = "Password123!")
    private String password;
}
