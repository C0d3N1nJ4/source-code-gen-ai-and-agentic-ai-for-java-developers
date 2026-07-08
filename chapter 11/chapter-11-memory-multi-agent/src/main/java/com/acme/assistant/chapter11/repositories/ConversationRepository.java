package com.acme.assistant.chapter11.repositories;

import com.acme.assistant.chapter11.entities.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for conversation metadata.
 */
public interface ConversationRepository extends JpaRepository<ConversationEntity, String> {
}
