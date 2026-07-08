package com.acme.assistant.repository;

import com.acme.assistant.entity.LeaveRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequestEntity, String> {
}
