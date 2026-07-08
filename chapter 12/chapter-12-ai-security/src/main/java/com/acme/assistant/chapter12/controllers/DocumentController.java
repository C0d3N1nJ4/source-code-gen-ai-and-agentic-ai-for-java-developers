package com.acme.assistant.chapter12.controllers;

import com.acme.assistant.chapter12.dto.DocumentCreateRequest;
import com.acme.assistant.chapter12.dto.DocumentResponse;
import com.acme.assistant.chapter12.rag.RagSecurityService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentController {

    private final RagSecurityService ragSecurityService;

    public DocumentController(RagSecurityService ragSecurityService) {
        this.ragSecurityService = ragSecurityService;
    }

    @GetMapping("/documents")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','FINANCE','OPERATIONS','ADMIN')")
    public List<DocumentResponse> documents(@RequestParam(required = false) String q) {
        return ragSecurityService.listAuthorizedDocuments(q);
    }

    @PostMapping("/documents")
    @PreAuthorize("hasAnyRole('HR','FINANCE','ADMIN')")
    public DocumentResponse createDocument(@Valid @RequestBody DocumentCreateRequest request) {
        return ragSecurityService.createDocument(request);
    }
}
