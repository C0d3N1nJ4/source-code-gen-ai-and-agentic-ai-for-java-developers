package com.acme.assistant.chapter11.memory;

import com.acme.assistant.chapter11.advisors.PromptTelemetryEstimator;
import com.acme.assistant.chapter11.dto.MemoryResponse;
import com.acme.assistant.chapter11.entities.ConversationEntity;
import com.acme.assistant.chapter11.entities.ConversationMessageEntity;
import com.acme.assistant.chapter11.repositories.ConversationMessageRepository;
import com.acme.assistant.chapter11.repositories.ConversationRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages conversation-memory persistence for concurrent users and multiple sessions.
 */
@Service
public class ConversationMemoryService {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository conversationMessageRepository;
    private final PromptTelemetryEstimator telemetryEstimator;

    public ConversationMemoryService(
            ConversationRepository conversationRepository,
            ConversationMessageRepository conversationMessageRepository,
            PromptTelemetryEstimator telemetryEstimator) {
        this.conversationRepository = conversationRepository;
        this.conversationMessageRepository = conversationMessageRepository;
        this.telemetryEstimator = telemetryEstimator;
    }

    @Transactional
    public String createConversationIfMissing(String requestedConversationId, String userId) {
        String conversationId = requestedConversationId == null || requestedConversationId.isBlank()
                ? UUID.randomUUID().toString()
                : requestedConversationId;

        if (conversationRepository.existsById(conversationId)) {
            return conversationId;
        }

        ConversationEntity entity = new ConversationEntity();
        entity.setConversationId(conversationId);
        entity.setUserId(userId);
        entity.setCreatedAt(OffsetDateTime.now());
        conversationRepository.save(entity);
        return conversationId;
    }

    @Transactional
    public void appendMessage(String conversationId, String userId, String role, String content) {
        ConversationMessageEntity message = new ConversationMessageEntity();
        message.setConversationId(conversationId);
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setEstimatedTokens(telemetryEstimator.estimateTokens(content));
        message.setCreatedAt(OffsetDateTime.now());
        conversationMessageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<String> shortTermContext(String conversationId, int limit) {
        List<ConversationMessageEntity> history = conversationMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId);
        int fromIndex = Math.max(0, history.size() - limit);
        return history.subList(fromIndex, history.size()).stream()
                .map(turn -> turn.getRole() + ": " + turn.getContent())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> crossConversationMemory(String userId, String conversationId) {
        return conversationMessageRepository
                .findTop20ByUserIdAndConversationIdNotOrderByCreatedAtDesc(userId, conversationId)
                .stream()
                .map(turn -> turn.getRole() + ": " + turn.getContent())
                .toList();
    }

    @Transactional(readOnly = true)
    public MemoryResponse getMemory(String conversationId) {
        List<MemoryResponse.MemoryTurn> turns = conversationMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(message -> new MemoryResponse.MemoryTurn(
                        message.getRole(),
                        message.getContent(),
                        message.getCreatedAt().toString()))
                .toList();

        return new MemoryResponse(conversationId, turns);
    }
}
