package com.acme.assistant.tool;

import com.acme.assistant.model.KnowledgeResult;
import com.acme.assistant.search.KnowledgeSearchCriteria;
import com.acme.assistant.service.KnowledgeSearchService;
import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeTools {

    private final KnowledgeSearchService knowledgeSearchService;

    public KnowledgeTools(KnowledgeSearchService knowledgeSearchService) {
        this.knowledgeSearchService = knowledgeSearchService;
    }

    @Tool(description = "Search enterprise knowledge documents for policy and procedural context.")
    public List<KnowledgeResult> searchKnowledge(String query) {
        return knowledgeSearchService.search(new KnowledgeSearchCriteria(query, 4));
    }
}
