package io.petprojects.bookshelfs.repository;

import io.petprojects.bookshelfs.entity.BookShareEntity;
import io.petprojects.bookshelfs.entity.enums.BookShareStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookShareRepository extends JpaRepository<BookShareEntity, Long> {
    Optional<BookShareEntity> findByBookIdAndRequesterIdAndShareStatus(Long bookId, Long requesterId,
                                                                       BookShareStatus shareStatus);

    @Query("SELECT bs FROM BookShareEntity bs WHERE bs.owner.id = :readerId OR bs.requester.id = :readerId")
    List<BookShareEntity> findAllByReaderId(@Param("readerId") Long readerId);
}
