package com.acme.assistant.chapter11.controllers;

import com.acme.assistant.chapter11.dto.MemoryResponse;
import com.acme.assistant.chapter11.memory.ConversationMemoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read API for conversation memory.
 */
@RestController
public class MemoryController {

    private final ConversationMemoryService conversationMemoryService;

    public MemoryController(ConversationMemoryService conversationMemoryService) {
        this.conversationMemoryService = conversationMemoryService;
    }

    @GetMapping("/memory/{conversationId}")
    public MemoryResponse getMemory(@PathVariable String conversationId) {
        return conversationMemoryService.getMemory(conversationId);
    }
}
