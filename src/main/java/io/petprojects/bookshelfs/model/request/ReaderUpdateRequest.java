package io.petprojects.bookshelfs.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReaderUpdateRequest {
    @NotNull
    @Pattern(regexp = "^(?=[a-zA-Z0-9._]{3,32}$)(?!.*[_.]{2})[^_.].*[^_.]$")
    @Schema(example = "testuser")
    private String username;

    @NotNull
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$")
    @Schema(example = "Password123!")
    private String password;

    @NotNull
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$")
    @Schema(example = "Password123!")
    private String passwordRepeated;

    @Schema(example = "testName")
    private String publicName;
}
