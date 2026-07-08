package com.acme.assistant.search;

public record KnowledgeSearchCriteria(
        String query,
        Integer topK) {
}
