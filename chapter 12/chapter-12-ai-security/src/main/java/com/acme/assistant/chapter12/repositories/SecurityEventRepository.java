package com.acme.assistant.chapter12.repositories;

import com.acme.assistant.chapter12.entities.SecurityEventEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityEventRepository extends JpaRepository<SecurityEventEntity, Long> {
    List<SecurityEventEntity> findTop200ByOrderByTimestampDesc();
}
