package com.example.chapter13.service;

import com.example.chapter13.model.EnterpriseKnowledgeDocument;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class AcmePlatformContextService {

    private final Chapter12BaselineAssistantService baselineAssistantService;

    public AcmePlatformContextService(Chapter12BaselineAssistantService baselineAssistantService) {
        this.baselineAssistantService = baselineAssistantService;
    }

    public List<EnterpriseKnowledgeDocument> knowledgeDocuments() {
        return baselineAssistantService.authorizedKnowledgeDocuments();
    }

    public List<String> mcpTools() {
        return baselineAssistantService.allowedMcpCapabilities().values().stream()
                .flatMap(capabilities -> capabilities.stream())
                .sorted()
                .toList();
    }

    public List<String> specialistAgents() {
        return baselineAssistantService.specialistAgents().stream()
                .map(agent -> "KNOWLEDGE".equals(agent) ? "Knowledge Agent" : agent + " Agent")
                .toList();
    }

    public Map<String, String> userProfile() {
        return baselineAssistantService.userProfile();
    }

    public List<String> semanticMemories() {
        return baselineAssistantService.semanticMemories();
    }

    public List<String> securityRoles() {
        return baselineAssistantService.securityRoles();
    }

    public String selectedAgent(String message) {
        return baselineAssistantService.selectAgent(message);
    }

    public Map<String, Set<String>> mcpCapabilitiesByAgent() {
        return baselineAssistantService.allowedMcpCapabilities();
    }

    public List<String> governanceSensitiveOperations() {
        return baselineAssistantService.governanceSensitiveOperations();
    }
}