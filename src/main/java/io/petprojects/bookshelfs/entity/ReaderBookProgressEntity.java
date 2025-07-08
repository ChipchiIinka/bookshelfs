package io.petprojects.bookshelfs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reader_book_progress")
@Getter
@Setter
@NoArgsConstructor
public class ReaderBookProgressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int lastReadPage;

    @ManyToOne
    private ReaderEntity reader;

    @ManyToOne
    private BookEntity book;
}
