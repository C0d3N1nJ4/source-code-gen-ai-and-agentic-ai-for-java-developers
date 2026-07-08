package com.acme.assistant.controller;

import com.acme.assistant.model.DocumentIngestionRequest;
import com.acme.assistant.model.DocumentIngestionResponse;
import com.acme.assistant.service.DocumentIngestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentIngestionService documentIngestionService;

    public DocumentController(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping("/ingest")
    public DocumentIngestionResponse ingest(@RequestBody(required = false) DocumentIngestionRequest request) {
        DocumentIngestionRequest safeRequest = request == null
                ? new DocumentIngestionRequest(null, null, null)
                : request;

        return documentIngestionService.ingest(safeRequest);
    }
}
