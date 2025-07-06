package io.petprojects.bookshelfs.repository;

import io.petprojects.bookshelfs.entity.BookshelfEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookshelfRepository extends JpaRepository<BookshelfEntity, Long> {
}
