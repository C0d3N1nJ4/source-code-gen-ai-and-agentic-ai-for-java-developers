package com.acme.assistant.chapter12.repositories;

import com.acme.assistant.chapter12.entities.DocumentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findByTitleContainingIgnoreCase(String title);
}
