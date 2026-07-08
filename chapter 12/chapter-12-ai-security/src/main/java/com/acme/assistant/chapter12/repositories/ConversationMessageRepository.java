package com.acme.assistant.chapter12.repositories;

import com.acme.assistant.chapter12.entities.ConversationMessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessageEntity, Long> {
    List<ConversationMessageEntity> findByConversationIdOrderByCreatedAtAsc(String conversationId);
}
