package com.acme.assistant.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.acme.assistant.agents.AgentOrchestratorService;
import com.acme.assistant.mcp.McpClientService;
import com.acme.assistant.memory.ConversationMemoryService;
import com.acme.assistant.observability.AiMetricsService;
import com.acme.assistant.observability.StructuredLoggingService;
import com.acme.assistant.rag.RagService;
import com.acme.assistant.routing.ModelRouterService;
import com.acme.assistant.routing.ModelSelection;
import com.acme.assistant.security.AuditLogService;
import com.acme.assistant.security.GuardrailService;
import com.acme.assistant.tools.ToolExecutionService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AssistantServiceTest {

    @Test
    void shouldReturnFallbackWhenGatewayFails() {
        PromptService promptService = Mockito.mock(PromptService.class);
        RagService ragService = Mockito.mock(RagService.class);
        ToolExecutionService toolExecutionService = Mockito.mock(ToolExecutionService.class);
        McpClientService mcpClientService = Mockito.mock(McpClientService.class);
        ConversationMemoryService memoryService = new ConversationMemoryService();
        AgentOrchestratorService agentService = Mockito.mock(AgentOrchestratorService.class);
        ModelRouterService routerService = Mockito.mock(ModelRouterService.class);
        GuardrailService guardrailService = Mockito.mock(GuardrailService.class);
        StructuredLoggingService loggingService = Mockito.mock(StructuredLoggingService.class);
        AiMetricsService metricsService = Mockito.mock(AiMetricsService.class);
        AuditLogService auditLogService = Mockito.mock(AuditLogService.class);
        LlmGatewayService llmGatewayService = Mockito.mock(LlmGatewayService.class);

        when(guardrailService.shouldBlock(any())).thenReturn(false);
        when(guardrailService.sanitizePrompt(any())).thenAnswer(i -> i.getArgument(0));
        when(routerService.selectModel(any())).thenReturn(new ModelSelection("qwen3:8b", "default", false));
        when(ragService.query(any())).thenReturn(new RagService.RagResult("context", List.of("doc-1")));
        when(promptService.buildSystemPrompt(any(), any())).thenReturn("sys");
        when(promptService.buildUserPrompt(any(), any(), any())).thenReturn("usr");
        when(llmGatewayService.ask(any(), any())).thenThrow(new RuntimeException("down"));
        when(llmGatewayService.fallback(any())).thenReturn("fallback");

        AssistantService service = new AssistantService(
            promptService, ragService, toolExecutionService, mcpClientService, memoryService, agentService,
            routerService, guardrailService, loggingService, metricsService, auditLogService, llmGatewayService
        );

        AssistantService.ChatResponse response = service.chat(
            new AssistantService.ChatRequest("conv-1", "user-1", "hello", Map.of())
        );

        assertThat(response.fallbackUsed()).isTrue();
        assertThat(response.answer()).isEqualTo("fallback");
        assertThat(memoryService.history("conv-1")).hasSize(2);
    }
}
