package com.acme.assistant.controller;

import com.acme.assistant.model.DocumentIngestionRequest;
import com.acme.assistant.model.DocumentIngestionResponse;
import com.acme.assistant.service.KnowledgeBaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class DocumentIngestionController {

    private final KnowledgeBaseService knowledgeBaseService;

    public DocumentIngestionController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<DocumentIngestionResponse> ingest(@Valid @RequestBody DocumentIngestionRequest request) {
        return ResponseEntity.ok(knowledgeBaseService.ingest(request));
    }

}
