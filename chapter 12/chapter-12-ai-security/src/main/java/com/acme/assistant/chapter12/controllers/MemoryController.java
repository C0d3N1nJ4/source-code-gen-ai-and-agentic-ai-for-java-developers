package com.acme.assistant.chapter12.controllers;

import com.acme.assistant.chapter12.dto.MemoryResponse;
import com.acme.assistant.chapter12.memory.ConversationMemoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemoryController {

    private final ConversationMemoryService conversationMemoryService;

    public MemoryController(ConversationMemoryService conversationMemoryService) {
        this.conversationMemoryService = conversationMemoryService;
    }

    @GetMapping("/memory/{conversationId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','FINANCE','OPERATIONS','ADMIN')")
    public MemoryResponse memory(@PathVariable String conversationId) {
        return conversationMemoryService.getMemory(conversationId);
    }
}
