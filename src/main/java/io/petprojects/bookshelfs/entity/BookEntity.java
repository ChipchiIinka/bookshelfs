package io.petprojects.bookshelfs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String contentType;

    private String description;

    private byte[] data;

    private boolean isGivenToRead;

    private boolean isEternalAccess;

    private LocalDateTime bookReturnDeadLine;

    @ManyToOne(fetch = FetchType.EAGER)
    private ReaderEntity bookOwner;

    @ManyToOne(fetch = FetchType.EAGER)
    private BookshelfEntity bookshelf;
}
