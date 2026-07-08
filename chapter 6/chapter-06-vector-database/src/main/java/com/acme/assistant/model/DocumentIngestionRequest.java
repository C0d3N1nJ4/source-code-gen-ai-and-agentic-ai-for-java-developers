package com.acme.assistant.model;

import jakarta.validation.Valid;
import java.util.List;

public record DocumentIngestionRequest(
        @Valid List<PolicyDocumentPayload> documents,
        Integer chunkSize,
        Integer overlap) {
}
