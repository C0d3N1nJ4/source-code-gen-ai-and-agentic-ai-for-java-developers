package com.acme.assistant.chapter11.controllers;

import com.acme.assistant.chapter11.dto.ChatRequest;
import com.acme.assistant.chapter11.dto.ChatResponse;
import com.acme.assistant.chapter11.services.EnterpriseAssistantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Chat API entrypoint for end users.
 */
@RestController
public class ChatController {

    private final EnterpriseAssistantService enterpriseAssistantService;

    public ChatController(EnterpriseAssistantService enterpriseAssistantService) {
        this.enterpriseAssistantService = enterpriseAssistantService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return enterpriseAssistantService.chat(null, request.userId(), request.message());
    }

    @PostMapping("/chat/{conversationId}")
    public ChatResponse chatWithConversation(@PathVariable String conversationId, @Valid @RequestBody ChatRequest request) {
        return enterpriseAssistantService.chat(conversationId, request.userId(), request.message());
    }
}
