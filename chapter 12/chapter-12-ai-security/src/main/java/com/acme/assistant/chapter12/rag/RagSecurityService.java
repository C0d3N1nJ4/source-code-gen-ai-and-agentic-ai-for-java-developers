package com.acme.assistant.chapter12.rag;

import com.acme.assistant.chapter12.dto.DocumentCreateRequest;
import com.acme.assistant.chapter12.dto.DocumentResponse;
import com.acme.assistant.chapter12.entities.DocumentEntity;
import com.acme.assistant.chapter12.exceptions.BadRequestException;
import com.acme.assistant.chapter12.exceptions.NotFoundException;
import com.acme.assistant.chapter12.repositories.DocumentRepository;
import com.acme.assistant.chapter12.security.AuthorizationService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RagSecurityService {

    private final DocumentRepository documentRepository;
    private final AuthorizationService authorizationService;

    public RagSecurityService(DocumentRepository documentRepository, AuthorizationService authorizationService) {
        this.documentRepository = documentRepository;
        this.authorizationService = authorizationService;
    }

    public DocumentResponse createDocument(DocumentCreateRequest request) {
        validateClassification(request.classification());
        DocumentEntity document = new DocumentEntity();
        document.setTitle(request.title());
        document.setClassification(request.classification().toUpperCase(Locale.ROOT));
        document.setOwnerUserId(request.ownerUserId());
        document.setContent(request.content());
        document.setSanitizedContent(sanitizeContent(request.content()));
        document.setMetadataJson(request.metadata().toString());
        document.setTrustedMetadata(isTrustedMetadata(request.metadata()));
        document.setCreatedAt(Instant.now());
        DocumentEntity saved = documentRepository.save(document);
        return new DocumentResponse(saved.getId(), saved.getTitle(), saved.getClassification(), saved.getOwnerUserId(), saved.isTrustedMetadata());
    }

    public List<DocumentResponse> listAuthorizedDocuments(String query) {
        List<DocumentEntity> source = query == null || query.isBlank()
                ? documentRepository.findAll()
                : documentRepository.findByTitleContainingIgnoreCase(query);

        List<DocumentResponse> allowed = new ArrayList<>();
        for (DocumentEntity document : source) {
            authorizationService.assertCanReadDocument(document);
            allowed.add(new DocumentResponse(
                    document.getId(),
                    document.getTitle(),
                    document.getClassification(),
                    document.getOwnerUserId(),
                    document.isTrustedMetadata()));
        }
        return allowed;
    }

    public List<String> retrieveSanitizedDocumentChunksForUser(String query) {
        List<DocumentEntity> source = query == null || query.isBlank()
                ? documentRepository.findAll()
                : documentRepository.findByTitleContainingIgnoreCase(query);

        List<String> chunks = new ArrayList<>();
        for (DocumentEntity document : source) {
            try {
                authorizationService.assertCanReadDocument(document);
                if (!document.isTrustedMetadata()) {
                    continue;
                }
                chunks.add(document.getSanitizedContent());
            } catch (RuntimeException ignored) {
                // Unauthorized documents are intentionally skipped to prevent data leakage.
            }
        }
        return chunks;
    }

    public DocumentEntity documentById(Long id) {
        return documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found: " + id));
    }

    private void validateClassification(String classification) {
        String normalized = classification.toUpperCase(Locale.ROOT);
        if (!normalized.equals("PUBLIC") && !normalized.equals("INTERNAL") && !normalized.equals("CONFIDENTIAL")) {
            throw new BadRequestException("Invalid classification. Allowed values: PUBLIC, INTERNAL, CONFIDENTIAL");
        }
    }

    private boolean isTrustedMetadata(Map<String, Object> metadata) {
        Object source = metadata.get("source");
        Object checksum = metadata.get("checksum");
        return source != null && checksum != null;
    }

    private String sanitizeContent(String content) {
        String sanitized = content.replaceAll("(?i)ignore previous instructions", "[REMOVED]");
        sanitized = sanitized.replaceAll("(?i)reveal system prompt", "[REMOVED]");
        sanitized = sanitized.replaceAll("(?i)bypass security", "[REMOVED]");
        return sanitized;
    }
}
