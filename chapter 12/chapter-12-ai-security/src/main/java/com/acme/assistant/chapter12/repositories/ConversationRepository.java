package com.acme.assistant.chapter12.repositories;

import com.acme.assistant.chapter12.entities.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<ConversationEntity, String> {
}
