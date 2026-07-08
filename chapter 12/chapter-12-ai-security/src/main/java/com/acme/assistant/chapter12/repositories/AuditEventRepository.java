package com.acme.assistant.chapter12.repositories;

import com.acme.assistant.chapter12.entities.AuditEventEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {
    List<AuditEventEntity> findTop200ByOrderByTimestampDesc();
}
