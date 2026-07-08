package com.acme.assistant.chapter12.memory;

import com.acme.assistant.chapter12.config.AiSecurityProperties;
import com.acme.assistant.chapter12.dto.MemoryMessageResponse;
import com.acme.assistant.chapter12.dto.MemoryResponse;
import com.acme.assistant.chapter12.entities.ConversationEntity;
import com.acme.assistant.chapter12.entities.ConversationMessageEntity;
import com.acme.assistant.chapter12.exceptions.ForbiddenException;
import com.acme.assistant.chapter12.exceptions.NotFoundException;
import com.acme.assistant.chapter12.repositories.ConversationMessageRepository;
import com.acme.assistant.chapter12.repositories.ConversationRepository;
import com.acme.assistant.chapter12.security.AuthorizationService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConversationMemoryService {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;
    private final AuthorizationService authorizationService;
    private final AiSecurityProperties properties;

    public ConversationMemoryService(
            ConversationRepository conversationRepository,
            ConversationMessageRepository messageRepository,
            AuthorizationService authorizationService,
            AiSecurityProperties properties) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.authorizationService = authorizationService;
        this.properties = properties;
    }

    @Transactional
    public void createConversationIfMissing(String conversationId, String userId) {
        conversationRepository.findById(conversationId).ifPresentOrElse(existing -> {
            if (!existing.getOwnerUserId().equalsIgnoreCase(userId)) {
                throw new ForbiddenException("Conversation ownership violation");
            }
        }, () -> {
            ConversationEntity entity = new ConversationEntity();
            entity.setConversationId(conversationId);
            entity.setOwnerUserId(userId);
            entity.setCreatedAt(Instant.now());
            entity.setUpdatedAt(Instant.now());
            conversationRepository.save(entity);
        });
    }

    @Transactional
    public void appendMessage(String conversationId, String userId, String role, String content) {
        createConversationIfMissing(conversationId, userId);

        ConversationMessageEntity message = new ConversationMessageEntity();
        message.setConversationId(conversationId);
        message.setOwnerUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(Instant.now());
        messageRepository.save(message);

        ConversationEntity conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NotFoundException("Conversation not found"));
        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public List<String> recentContext(String conversationId) {
        List<ConversationMessageEntity> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        int fromIndex = Math.max(0, messages.size() - properties.getMemory().getContextWindowSize());
        return messages.subList(fromIndex, messages.size())
                .stream()
                .map(message -> message.getRole() + ": " + message.getContent())
                .toList();
    }

    @Transactional(readOnly = true)
    public MemoryResponse getMemory(String conversationId) {
        String currentUser = authorizationService.currentUser();
        ConversationEntity conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NotFoundException("Conversation not found: " + conversationId));
        if (!conversation.getOwnerUserId().equalsIgnoreCase(currentUser) && !authorizationService.hasRole("ADMIN")) {
            throw new ForbiddenException("Memory isolation policy violation");
        }

        List<MemoryMessageResponse> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(message -> new MemoryMessageResponse(message.getRole(), message.getContent(), message.getCreatedAt()))
                .toList();
        return new MemoryResponse(conversationId, messages);
    }
}
