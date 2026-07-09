package com.example.chapter13.controller;

import com.example.chapter13.dto.AssistantRuntimeDtos.ApprovalDecisionRequest;
import com.example.chapter13.dto.AssistantRuntimeDtos.ApprovalResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.AuditEventResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.ChatRequest;
import com.example.chapter13.dto.AssistantRuntimeDtos.ChatResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.DocumentCreateRequest;
import com.example.chapter13.dto.AssistantRuntimeDtos.DocumentResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.MemoryResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.SecurityEventResponse;
import com.example.chapter13.service.AssistantRuntimeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssistantContinuationController {

    private final AssistantRuntimeService assistantRuntimeService;

    public AssistantContinuationController(AssistantRuntimeService assistantRuntimeService) {
        this.assistantRuntimeService = assistantRuntimeService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return assistantRuntimeService.startConversation(request);
    }

    @PostMapping("/chat/{conversationId}")
    public ChatResponse continueChat(@PathVariable String conversationId, @Valid @RequestBody ChatRequest request) {
        return assistantRuntimeService.continueConversation(conversationId, request);
    }

    @GetMapping("/memory/{conversationId}")
    public MemoryResponse memory(@PathVariable String conversationId) {
        return assistantRuntimeService.getMemory(conversationId);
    }

    @GetMapping("/documents")
    public List<DocumentResponse> documents(@RequestParam(required = false) String q) {
        return assistantRuntimeService.listAuthorizedDocuments(q);
    }

    @PostMapping("/documents")
    public DocumentResponse createDocument(@Valid @RequestBody DocumentCreateRequest request) {
        return assistantRuntimeService.createDocument(request);
    }

    @GetMapping("/approvals")
    public List<ApprovalResponse> approvals() {
        return assistantRuntimeService.listApprovals();
    }

    @PostMapping("/approvals/{id}/approve")
    public ApprovalResponse approve(@PathVariable String id, @RequestBody(required = false) ApprovalDecisionRequest request) {
        return assistantRuntimeService.approve(id);
    }

    @GetMapping("/audit")
    public List<AuditEventResponse> audit() {
        return assistantRuntimeService.audit();
    }

    @GetMapping("/security/events")
    public List<SecurityEventResponse> securityEvents() {
        return assistantRuntimeService.securityEvents();
    }
}