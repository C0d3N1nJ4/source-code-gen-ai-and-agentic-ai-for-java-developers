package com.acme.assistant.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record DocumentIngestionRequest(
        List<@Valid SourceDocument> documents,
        @Positive(message = "chunkSize must be greater than zero") Integer chunkSize) {
}
