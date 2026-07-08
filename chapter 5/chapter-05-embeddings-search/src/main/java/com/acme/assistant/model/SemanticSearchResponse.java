package com.acme.assistant.model;

import java.util.List;

public record SemanticSearchResponse(
        String query,
        int totalResults,
        List<SearchResult> results) {
}
