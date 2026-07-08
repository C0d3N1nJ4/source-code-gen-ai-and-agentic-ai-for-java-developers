package com.acme.assistant.chapter12.controllers;

import com.acme.assistant.chapter12.dto.ChatRequest;
import com.acme.assistant.chapter12.dto.ChatResponse;
import com.acme.assistant.chapter12.services.ChatOrchestrationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatOrchestrationService chatOrchestrationService;

    public ChatController(ChatOrchestrationService chatOrchestrationService) {
        this.chatOrchestrationService = chatOrchestrationService;
    }

    @PostMapping("/chat")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','FINANCE','OPERATIONS','ADMIN')")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatOrchestrationService.startConversation(request);
    }

    @PostMapping("/chat/{conversationId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','FINANCE','OPERATIONS','ADMIN')")
    public ChatResponse continueChat(@PathVariable String conversationId, @Valid @RequestBody ChatRequest request) {
        return chatOrchestrationService.continueConversation(conversationId, request);
    }
}
