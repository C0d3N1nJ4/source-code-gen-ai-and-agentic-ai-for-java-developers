package com.acme.assistant.model;

import java.util.List;

public record DocumentIngestionRequest(
        List<String> documentIds,
        Integer chunkSize,
        Integer overlap) {
}
