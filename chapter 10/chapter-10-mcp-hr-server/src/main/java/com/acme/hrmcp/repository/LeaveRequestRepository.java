package com.acme.hrmcp.repository;

import com.acme.hrmcp.entity.LeaveRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequestEntity, UUID> {
}
