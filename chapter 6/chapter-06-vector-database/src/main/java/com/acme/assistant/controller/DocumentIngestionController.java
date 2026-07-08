package com.acme.assistant.controller;

import com.acme.assistant.model.DocumentIngestionRequest;
import com.acme.assistant.model.DocumentIngestionResponse;
import com.acme.assistant.service.DocumentIngestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class DocumentIngestionController {

    private final DocumentIngestionService ingestionService;

    public DocumentIngestionController(DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/ingest")
    public DocumentIngestionResponse ingest(@Valid @RequestBody(required = false) DocumentIngestionRequest request) {
        DocumentIngestionRequest safeRequest = request == null
                ? new DocumentIngestionRequest(null, null, null)
                : request;
        return ingestionService.ingest(safeRequest);
    }
}
