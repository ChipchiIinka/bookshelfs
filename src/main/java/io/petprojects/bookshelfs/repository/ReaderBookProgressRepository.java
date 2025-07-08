package io.petprojects.bookshelfs.repository;

import io.petprojects.bookshelfs.entity.ReaderBookProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReaderBookProgressRepository extends JpaRepository<ReaderBookProgressEntity, Long> {
    Optional<ReaderBookProgressEntity> findByReaderIdAndBookId (Long readerId, Long bookId);
}
