package com.acme.assistant.chapter11.repositories;

import com.acme.assistant.chapter11.entities.BusinessEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for business event memory records.
 */
public interface BusinessEventRepository extends JpaRepository<BusinessEvent, Long> {

    List<BusinessEvent> findAllByOrderByCreatedAtDesc();

    List<BusinessEvent> findTop50ByUserIdOrderByCreatedAtDesc(String userId);
}
