package io.petprojects.bookshelfs.repository;

import io.petprojects.bookshelfs.entity.JwtBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklistEntity, String> {
}
