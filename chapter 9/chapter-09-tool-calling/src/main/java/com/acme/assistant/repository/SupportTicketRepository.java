package com.acme.assistant.repository;

import com.acme.assistant.entity.SupportTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketRepository extends JpaRepository<SupportTicketEntity, String> {
}
