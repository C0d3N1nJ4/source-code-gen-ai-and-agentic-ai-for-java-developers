package com.acme.assistant.chat;

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
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    private static final Logger log = LoggerFactory.getLogger(AssistantService.class);

    private final PromptService promptService;
    private final RagService ragService;
    private final ToolExecutionService toolExecutionService;
    private final McpClientService mcpClientService;
    private final ConversationMemoryService memoryService;
    private final AgentOrchestratorService orchestratorService;
    private final ModelRouterService modelRouterService;
    private final GuardrailService guardrailService;
    private final StructuredLoggingService structuredLoggingService;
    private final AiMetricsService metricsService;
    private final AuditLogService auditLogService;
    private final LlmGatewayService llmGatewayService;

    public AssistantService(PromptService promptService,
                            RagService ragService,
                            ToolExecutionService toolExecutionService,
                            McpClientService mcpClientService,
                            ConversationMemoryService memoryService,
                            AgentOrchestratorService orchestratorService,
                            ModelRouterService modelRouterService,
                            GuardrailService guardrailService,
                            StructuredLoggingService structuredLoggingService,
                            AiMetricsService metricsService,
                            AuditLogService auditLogService,
                            LlmGatewayService llmGatewayService) {
        this.promptService = promptService;
        this.ragService = ragService;
        this.toolExecutionService = toolExecutionService;
        this.mcpClientService = mcpClientService;
        this.memoryService = memoryService;
        this.orchestratorService = orchestratorService;
        this.modelRouterService = modelRouterService;
        this.guardrailService = guardrailService;
        this.structuredLoggingService = structuredLoggingService;
        this.metricsService = metricsService;
        this.auditLogService = auditLogService;
        this.llmGatewayService = llmGatewayService;
    }

    public ChatResponse chat(ChatRequest request) {
        long started = System.currentTimeMillis();
        metricsService.recordRequest();

        if (guardrailService.shouldBlock(request.message())) {
            return new ChatResponse(request.conversationId(), "Request blocked by guardrails", List.of(), "guardrails", true);
        }

        String sanitizedPrompt = guardrailService.sanitizePrompt(request.message());
        ModelSelection selection = modelRouterService.selectModel(sanitizedPrompt);
        metricsService.recordModelSelected(selection.model());

        RagService.RagResult ragResult = ragService.query(sanitizedPrompt);
        String systemPrompt = promptService.buildSystemPrompt(selection.model(), request.userId());
        String userPrompt = promptService.buildUserPrompt(sanitizedPrompt, request.context(), ragResult.context());

        String answer;
        boolean fallbackUsed = false;
        try {
            answer = llmGatewayService.ask(systemPrompt, userPrompt);
        } catch (Exception ex) {
            log.warn("Primary model call failed", ex);
            metricsService.recordFallback();
            fallbackUsed = true;
            answer = llmGatewayService.fallback(sanitizedPrompt);
        }

        memoryService.append(request.conversationId(), "user", sanitizedPrompt);
        memoryService.append(request.conversationId(), "assistant", answer);
        auditLogService.log(request.userId(), "CHAT", request.conversationId());

        long latencyMs = System.currentTimeMillis() - started;
        metricsService.recordModelLatency(selection.model(), latencyMs);
        metricsService.recordEstimatedTokens((sanitizedPrompt.length() + answer.length()) / 4);

        structuredLoggingService.logAiRequest(
            UUID.randomUUID().toString(),
            request.userId(),
            request.conversationId(),
            selection.model(),
            "Supervisor Agent",
            latencyMs,
            0,
            0,
            fallbackUsed
        );

        return new ChatResponse(request.conversationId(), answer, ragResult.citations(), selection.model(), fallbackUsed);
    }

    public RagResponse ragQuery(RagQueryRequest request) {
        RagService.RagResult ragResult = ragService.query(request.query());
        String response = ragResult.context().isBlank() ? "No relevant enterprise documents found" : ragResult.context();
        return new RagResponse(response, ragResult.citations());
    }

    public ToolResponse executeTool(ToolRequest request) {
        String result = toolExecutionService.execute(request.toolName(), request.arguments());
        return new ToolResponse(request.toolName(), result);
    }

    public AgentResponse runAgent(AgentRequest request) {
        AgentOrchestratorService.AgentResult result = orchestratorService.run(request.instruction());
        return new AgentResponse(result.agent(), result.output());
    }

    public McpResponse invokeMcp(String operation, Map<String, Object> payload) {
        String response = mcpClientService.invoke(operation, payload);
        return new McpResponse(operation, response);
    }

    public record ChatRequest(String conversationId, String userId, String message, Map<String, Object> context) {
    }

    public record ChatResponse(String conversationId, String answer, List<String> citations, String selectedModel, boolean fallbackUsed) {
    }

    public record RagQueryRequest(String query) {
    }

    public record RagResponse(String answer, List<String> citations) {
    }

    public record ToolRequest(String toolName, Map<String, Object> arguments) {
    }

    public record ToolResponse(String toolName, String output) {
    }

    public record AgentRequest(String instruction) {
    }

    public record AgentResponse(String agent, String output) {
    }

    public record McpResponse(String operation, String output) {
    }
}
