package com.webapp.webapp.repository;

import com.webapp.webapp.repository.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<BookEntity,UUID> {
    Optional<BookEntity> findById(UUID id);
}
