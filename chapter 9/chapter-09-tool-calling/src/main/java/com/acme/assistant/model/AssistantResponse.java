package com.acme.assistant.model;

import java.time.Instant;
import java.util.List;

public record AssistantResponse(
        String question,
        String answer,
        List<SourceDocument> sources,
        Instant timestamp) {
}
