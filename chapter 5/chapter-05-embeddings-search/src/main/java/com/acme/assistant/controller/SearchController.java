package com.acme.assistant.controller;

import com.acme.assistant.model.SemanticSearchRequest;
import com.acme.assistant.model.SemanticSearchResponse;
import com.acme.assistant.service.SemanticSearchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SemanticSearchService semanticSearchService;

    public SearchController(SemanticSearchService semanticSearchService) {
        this.semanticSearchService = semanticSearchService;
    }

    @PostMapping("/semantic")
    public SemanticSearchResponse semanticSearch(@Valid @RequestBody SemanticSearchRequest request) {
        return semanticSearchService.search(request);
    }
}
