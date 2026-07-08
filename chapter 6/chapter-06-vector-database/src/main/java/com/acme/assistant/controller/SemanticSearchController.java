package com.acme.assistant.controller;

import com.acme.assistant.model.SemanticSearchRequest;
import com.acme.assistant.model.SemanticSearchResponse;
import com.acme.assistant.search.SemanticSearchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SemanticSearchController {

    private final SemanticSearchService semanticSearchService;

    public SemanticSearchController(SemanticSearchService semanticSearchService) {
        this.semanticSearchService = semanticSearchService;
    }

    @PostMapping("/vector")
    public SemanticSearchResponse vectorSearch(@Valid @RequestBody SemanticSearchRequest request) {
        return semanticSearchService.search(request);
    }
}
