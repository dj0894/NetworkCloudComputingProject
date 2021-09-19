package com.webapp.webapp.repository;

import com.webapp.webapp.repository.model.BookEntity;
import com.webapp.webapp.repository.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageEntity, UUID> {

    Optional<ImageEntity> findById(UUID id);

    List<ImageEntity> findAllByBookId(BookEntity bookId);

    Optional<ImageEntity> findByS3ObjectName(String s3ObjectName);

}
