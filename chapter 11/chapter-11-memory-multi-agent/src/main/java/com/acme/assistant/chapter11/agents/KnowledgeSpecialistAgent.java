package com.acme.assistant.chapter11.agents;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Knowledge specialist agent for enterprise documentation and standards.
 */
@Component
public class KnowledgeSpecialistAgent implements SpecialistAgent {

    @Override
    public AgentType type() {
        return AgentType.KNOWLEDGE;
    }

    @Override
    public boolean supports(String request) {
        String normalized = request.toLowerCase(Locale.ROOT);
        return normalized.contains("policy")
                || normalized.contains("architecture")
                || normalized.contains("guideline")
                || normalized.contains("standards")
                || normalized.contains("documentation");
    }

    @Override
    public AgentResult execute(AgentExecutionContext context) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("documents", context.ragSnippets());
        payload.put("memoryHints", context.longTermMemory());
        return new AgentResult(type(), "Knowledge context assembled from RAG and enterprise memory.", payload);
    }
}
