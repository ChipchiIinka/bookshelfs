package io.petprojects.bookshelfs.entity;

import io.petprojects.bookshelfs.entity.enums.AccessType;
import io.petprojects.bookshelfs.entity.enums.BookShareStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "book_share")
@Getter
@Setter
@NoArgsConstructor
public class BookShareEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private BookShareStatus shareStatus;
    private AccessType accessType;
    private LocalDate expirationDate;
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.EAGER)
    private BookEntity book;

    @ManyToOne(fetch = FetchType.EAGER)
    private ReaderEntity requester;

    @ManyToOne(fetch = FetchType.EAGER)
    private ReaderEntity owner;
}
