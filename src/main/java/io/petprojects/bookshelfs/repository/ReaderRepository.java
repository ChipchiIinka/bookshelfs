package io.petprojects.bookshelfs.repository;

import io.petprojects.bookshelfs.entity.ReaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<ReaderEntity, Long> {
    Optional<ReaderEntity> findByUsername(String username);
    Optional<ReaderEntity> findByVerificationCode(String verificationCode);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM ReaderEntity u WHERE u.username = :login OR u.email = :login")
    Optional<ReaderEntity> findByUsernameOrEmail(@Param("login") String login);
}
