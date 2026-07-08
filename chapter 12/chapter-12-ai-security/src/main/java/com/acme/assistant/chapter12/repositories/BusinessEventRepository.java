package com.acme.assistant.chapter12.repositories;

import com.acme.assistant.chapter12.entities.BusinessEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessEventRepository extends JpaRepository<BusinessEventEntity, Long> {
}
