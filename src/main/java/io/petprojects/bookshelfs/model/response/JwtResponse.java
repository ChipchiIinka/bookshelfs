package io.petprojects.bookshelfs.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
}
