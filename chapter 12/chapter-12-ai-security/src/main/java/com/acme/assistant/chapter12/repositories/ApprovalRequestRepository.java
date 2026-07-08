package com.acme.assistant.chapter12.repositories;

import com.acme.assistant.chapter12.entities.ApprovalRequestEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequestEntity, String> {
    List<ApprovalRequestEntity> findByStatusOrderByCreatedAtDesc(String status);
}
