package com.acme.assistant.model;

import java.util.List;

public record RagResponse(
        String question,
        String answer,
        List<SourceDocument> sources) {
}
