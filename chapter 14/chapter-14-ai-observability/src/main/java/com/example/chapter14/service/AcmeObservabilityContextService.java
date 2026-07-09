package com.example.chapter14.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AcmeObservabilityContextService {

    private final Chapter13BaselineAssistantService baselineAssistantService;

    public AcmeObservabilityContextService(Chapter13BaselineAssistantService baselineAssistantService) {
        this.baselineAssistantService = baselineAssistantService;
    }

    public List<String> participatingAgents() {
        return baselineAssistantService.agentSuite().cases().stream()
                .flatMap(currentCase -> currentCase.routedAgents().stream())
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(LinkedHashSet::new),
                        List::copyOf));
    }

    public List<String> contactedMcpServers() {
        return List.of("finance-mcp", "support-mcp", "engineering-mcp");
    }

    public List<String> retrievedDocuments() {
        return baselineAssistantService.defaultRagEvaluation().retrievedDocuments().stream()
                .map(Chapter13BaselineAssistantService.RetrievedDocument::documentId)
                .toList();
    }

    public Map<String, String> userProfile() {
        return baselineAssistantService.userProfile();
    }

    public List<String> semanticMemories() {
        return baselineAssistantService.semanticMemories();
    }

    public Map<String, Integer> alertThresholds() {
        return Map.of(
                "responseLatencyMs", 5000,
                "toolFailureRatePercent", 2,
                "mcpAvailabilityPercent", 99,
                "dailyBudgetUsedPercent", 85);
    }
}