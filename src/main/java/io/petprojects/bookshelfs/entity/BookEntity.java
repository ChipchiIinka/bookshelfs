package io.petprojects.bookshelfs.entity;

import io.petprojects.bookshelfs.entity.enums.BookStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private String description;
    private String filePath;
    private String htmlPath;
    private BookStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    private ReaderEntity bookOwner;

    @ManyToOne(fetch = FetchType.EAGER)
    private BookshelfEntity bookshelf;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<ReaderBookProgressEntity> readingProgresses;
}
