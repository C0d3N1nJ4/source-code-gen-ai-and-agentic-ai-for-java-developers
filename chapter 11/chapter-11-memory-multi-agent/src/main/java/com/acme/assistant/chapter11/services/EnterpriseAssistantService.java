package com.acme.assistant.chapter11.services;

import com.acme.assistant.chapter11.coordinator.CoordinatorAgentService;
import com.acme.assistant.chapter11.dto.ChatResponse;
import com.acme.assistant.chapter11.memory.ConversationMemoryService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates full chat lifecycle with memory updates and coordinator execution.
 */
@Service
public class EnterpriseAssistantService {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseAssistantService.class);

    private final ConversationMemoryService conversationMemoryService;
    private final CoordinatorAgentService coordinatorAgentService;
    private final BusinessEventService businessEventService;

    public EnterpriseAssistantService(
            ConversationMemoryService conversationMemoryService,
            CoordinatorAgentService coordinatorAgentService,
            BusinessEventService businessEventService) {
        this.conversationMemoryService = conversationMemoryService;
        this.coordinatorAgentService = coordinatorAgentService;
        this.businessEventService = businessEventService;
    }

    public ChatResponse chat(String requestedConversationId, String userId, String message) {
        String conversationId = conversationMemoryService.createConversationIfMissing(requestedConversationId, userId);
        conversationMemoryService.appendMessage(conversationId, userId, "user", message);

        List<String> shortTerm = conversationMemoryService.shortTermContext(conversationId, 8);
        List<String> longTerm = conversationMemoryService.crossConversationMemory(userId, conversationId);

        CoordinatorAgentService.CoordinationResult result = coordinatorAgentService.coordinate(
                userId,
                conversationId,
                message,
                shortTerm,
                longTerm);

        conversationMemoryService.appendMessage(conversationId, userId, "assistant", result.response());

        result.agentResults().forEach(agentResult -> {
            if (agentResult.agentType() != null) {
                businessEventService.record(
                        userId,
                        conversationId,
                        agentResult.agentType().name().toLowerCase() + "_agent_completed",
                        agentResult.summary());
            }
        });

        log.info("Request handled. conversationId={}, selectedAgentCount={}", conversationId, result.agentResults().size());

        return new ChatResponse(
                conversationId,
                result.response(),
                result.agentResults().stream()
                        .map(agentResult -> String.valueOf(agentResult.agentType()))
                        .toList(),
                result.responseTimeMs(),
                result.promptTokens(),
                result.responseTokens());
    }
}
