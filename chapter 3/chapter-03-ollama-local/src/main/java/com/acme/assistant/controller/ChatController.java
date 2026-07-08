package com.acme.assistant.controller;

import com.acme.assistant.model.AssistantResponse;
import com.acme.assistant.model.ChatRequest;
import com.acme.assistant.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public AssistantResponse chat(@Valid @RequestBody ChatRequest request) {
        String response = chatService.chat(request.message());
        return new AssistantResponse(response);
    }
}
