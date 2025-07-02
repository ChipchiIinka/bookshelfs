package io.petprojects.bookshelfs.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReaderResponse {

    public String username;
    public String publicName;
}
