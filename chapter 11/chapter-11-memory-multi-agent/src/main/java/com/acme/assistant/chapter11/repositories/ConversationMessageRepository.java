package com.acme.assistant.chapter11.repositories;

import com.acme.assistant.chapter11.entities.ConversationMessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for persisted chat turns.
 */
public interface ConversationMessageRepository extends JpaRepository<ConversationMessageEntity, Long> {

    List<ConversationMessageEntity> findByConversationIdOrderByCreatedAtAsc(String conversationId);

    List<ConversationMessageEntity> findTop20ByUserIdAndConversationIdNotOrderByCreatedAtDesc(String userId, String conversationId);
}
