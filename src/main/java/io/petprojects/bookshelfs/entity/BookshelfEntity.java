package io.petprojects.bookshelfs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bookshelfs")
public class BookshelfEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer bookshelfCapacity;

    @OneToMany(mappedBy = "bookshelf", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<BookEntity> books;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "reader_bookshelfs",
            joinColumns = @JoinColumn(name = "bookshelf_id"),
            inverseJoinColumns = @JoinColumn(name = "reader_id"))
    private ReaderEntity reader;
}
