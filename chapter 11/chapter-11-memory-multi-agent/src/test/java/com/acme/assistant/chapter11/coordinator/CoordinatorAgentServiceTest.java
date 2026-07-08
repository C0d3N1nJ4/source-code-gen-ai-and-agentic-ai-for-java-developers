package com.acme.assistant.chapter11.coordinator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.acme.assistant.chapter11.advisors.PromptTelemetryEstimator;
import com.acme.assistant.chapter11.agents.AgentExecutionContext;
import com.acme.assistant.chapter11.agents.AgentResult;
import com.acme.assistant.chapter11.agents.AgentType;
import com.acme.assistant.chapter11.agents.SpecialistAgent;
import com.acme.assistant.chapter11.config.AiPlatformProperties;
import com.acme.assistant.chapter11.rag.EnterpriseKnowledgeService;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;

class CoordinatorAgentServiceTest {

    @Test
    void shouldRouteToMatchingSpecialistAgents() {
        SpecialistAgent finance = new SpecialistAgent() {
            @Override
            public AgentType type() {
                return AgentType.FINANCE;
            }

            @Override
            public boolean supports(String request) {
                return request.contains("travel");
            }

            @Override
            public AgentResult execute(AgentExecutionContext context) {
                return new AgentResult(AgentType.FINANCE, "Finance done", java.util.Map.of("ok", true));
            }
        };

        EnterpriseKnowledgeService knowledgeService = org.mockito.Mockito.mock(EnterpriseKnowledgeService.class);
        when(knowledgeService.retrieve(anyString(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(List.of("Policy"));

        ChatModel chatModel = org.mockito.Mockito.mock(ChatModel.class);
        when(chatModel.call(anyString())).thenReturn("Unified response");

        CoordinatorAgentService service = new CoordinatorAgentService(
                List.of(finance),
                knowledgeService,
                new AiPlatformProperties("coord-prompt", 3, 8),
                new PromptTelemetryEstimator(),
                Executors.newFixedThreadPool(2),
                chatModel);

        CoordinatorAgentService.CoordinationResult result = service.coordinate(
                "EMP-1",
                "conv-1",
                "estimate travel expenses",
                List.of("user: hi"),
                List.of("past memory"));

        assertThat(result.agentResults()).hasSize(1);
        assertThat(result.response()).isEqualTo("Unified response");
    }
}
