package com.example.chapter14.service;

import com.example.chapter14.dto.ObservabilityRequests.TraceSimulationRequest;
import com.example.chapter14.dto.ObservabilityResponses.TraceSimulationResponse;
import com.example.chapter14.dto.ObservabilityResponses.TraceSpanView;
import com.example.chapter14.model.TraceSpan;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class TraceSimulationService {

    private final AcmeObservabilityContextService contextService;
    private final Tracer tracer;
    private final Chapter13BaselineAssistantService baselineAssistantService;

    public TraceSimulationService(
            AcmeObservabilityContextService contextService,
            Tracer tracer,
            Chapter13BaselineAssistantService baselineAssistantService) {
        this.contextService = contextService;
        this.tracer = tracer;
        this.baselineAssistantService = baselineAssistantService;
    }

    public TraceSimulationResponse simulate(TraceSimulationRequest request) {
        String requestId = mdcOrGenerated("requestId", "req");
        String conversationId = request.conversationId();
        String workflowId = mdcOrGenerated("workflowId", "workflow");
        String userId = request.userId();

        Span rootSpan = tracer.nextSpan().name("Authentication").start();
        String traceId;
        try (Tracer.SpanInScope ignored = tracer.withSpan(rootSpan)) {
            traceId = rootSpan.context().traceId();
        } finally {
            rootSpan.end();
        }

        var rag = baselineAssistantService.defaultRagEvaluation();
        var tools = baselineAssistantService.toolCallingSuite();
        var mcp = baselineAssistantService.mcpSuite();
        var agents = baselineAssistantService.agentSuite();
        var workflow = baselineAssistantService.workflowSuite();
        var memory = baselineAssistantService.defaultConversationEvaluation();

        List<TraceSpan> spans = request.includeFailurePath()
            ? failureAwareSpans(request, rag, tools, mcp, agents, memory)
            : successSpans(request, rag, tools, mcp, agents, memory);

        createCustomSpans();

        Map<String, Long> componentLatencyMs = new LinkedHashMap<>();
        for (TraceSpan span : spans) {
            componentLatencyMs.merge(span.component(), span.latencyMs(), Long::sum);
        }

        List<TraceSpanView> views = spans.stream()
                .map(span -> new TraceSpanView(
                        span.spanId().isBlank() ? rootSpan.context().spanId() : span.spanId(),
                        span.parentSpanId(),
                        span.name(),
                        span.component(),
                        span.latencyMs(),
                        span.status(),
                        span.tags()))
                .toList();

        return new TraceSimulationResponse(
                traceId,
                requestId,
                conversationId,
                workflowId,
                userId,
                workflow.suiteName(),
                views,
                componentLatencyMs,
                contextService.participatingAgents(),
                contextService.contactedMcpServers());
    }

            private List<TraceSpan> successSpans(
                TraceSimulationRequest request,
                Chapter13BaselineAssistantService.RagEvaluation rag,
                Chapter13BaselineAssistantService.ToolCallingSuite tools,
                Chapter13BaselineAssistantService.McpSuite mcp,
                Chapter13BaselineAssistantService.AgentSuite agents,
                Chapter13BaselineAssistantService.ConversationEvaluation memory) {
        return List.of(
                span("span-1", null, "Prompt Builder", "prompt", 28, "OK", promptTags(request.userId(), rag.expectedDomain())),
                span("span-2", "span-1", "Memory Retrieval", "memory", 19, "OK", memoryTags(request.userId(), memory)),
                span("span-3", "span-2", "Embedding Search", "embedding", 14, "OK", correlationTags(request.userId())),
                span("span-4", "span-3", "Vector Search", "rag", rag.metrics().retrievalLatencyMs(), "OK", ragTags(request.userId(), rag)),
                span("span-5", "span-4", "LLM Invocation", "llm", 410, "OK", correlationTags(request.userId())),
                span("span-6", "span-5", "Tool Calling", "tool", tools.metrics().averageToolLatencyMs(), "OK", toolTags(request.userId(), tools)),
                span("span-7", "span-6", "MCP Communication", "mcp", mcp.metrics().averageMcpLatencyMs(), "OK", mcpTags(request.userId(), mcp, "finance-mcp")),
                span("span-8", "span-7", "Coordinator Agent", "agent", 31, "OK", agentTags(request.userId(), "Coordinator Agent")),
                span("span-9", "span-8", "HR Agent", "agent", 22, "OK", agentTags(request.userId(), "HR Agent")),
                span("span-10", "span-8", "Finance Agent", "agent", 54, "OK", agentTags(request.userId(), "Finance Agent")),
                span("span-11", "span-8", "Knowledge Agent", "agent", 37, "OK", agentTags(request.userId(), agents.cases().get(1).routedAgents().getLast())),
                span("span-12", "span-11", "Audit Logging", "service", 12, "OK", correlationTags(request.userId())));
    }

            private List<TraceSpan> failureAwareSpans(
                TraceSimulationRequest request,
                Chapter13BaselineAssistantService.RagEvaluation rag,
                Chapter13BaselineAssistantService.ToolCallingSuite tools,
                Chapter13BaselineAssistantService.McpSuite mcp,
                Chapter13BaselineAssistantService.AgentSuite agents,
                Chapter13BaselineAssistantService.ConversationEvaluation memory) {
        return List.of(
                span("span-1", null, "Prompt Builder", "prompt", 32, "OK", promptTags(request.userId(), rag.expectedDomain())),
                span("span-2", "span-1", "Memory Retrieval", "memory", 22, "OK", memoryTags(request.userId(), memory)),
                span("span-3", "span-2", "Embedding Search", "embedding", 16, "OK", correlationTags(request.userId())),
                span("span-4", "span-3", "Vector Search", "rag", Math.max(49, rag.metrics().retrievalLatencyMs()), "OK", ragTags(request.userId(), rag)),
                span("span-5", "span-4", "LLM Invocation", "llm", 445, "OK", correlationTags(request.userId())),
                span("span-6", "span-5", "Tool Calling", "tool", Math.max(75, tools.metrics().averageToolLatencyMs()), "OK", toolTags(request.userId(), tools)),
                span("span-7", "span-6", "MCP Communication", "mcp", 510, "TIMEOUT", mcpTags(request.userId(), mcp, "finance-mcp")),
                span("span-8", "span-6", "MCP Communication", "mcp", 84, "OK", mcpTags(request.userId(), mcp, "support-mcp")),
                span("span-9", "span-8", "Coordinator Agent", "agent", 34, "OK", agentTags(request.userId(), "Coordinator Agent")),
                span("span-10", "span-9", "Finance Agent", "agent", 61, "OK", agentTags(request.userId(), "Finance Agent")),
                span("span-11", "span-9", "Knowledge Agent", "agent", 28, "OK", agentTags(request.userId(), agents.cases().get(1).routedAgents().getLast())),
                span("span-12", "span-11", "Audit Logging", "service", 15, "OK", correlationTags(request.userId())));
    }

    private void createCustomSpans() {
        List<String> spanNames = List.of(
                "Prompt Builder",
                "Memory Retrieval",
                "Embedding Search",
                "Vector Search",
                "LLM Invocation",
                "Tool Calling",
                "MCP Communication",
                "Coordinator Agent",
                "HR Agent",
                "Finance Agent",
                "Knowledge Agent",
                "Audit Logging");
        for (String spanName : spanNames) {
            Span span = tracer.nextSpan().name(spanName).start();
            span.end();
        }
    }

    private Map<String, String> correlationTags(String userId) {
        return Map.of(
                "traceId", mdcOrGenerated("traceId", "trace"),
                "requestId", mdcOrGenerated("requestId", "req"),
                "conversationId", mdcOrGenerated("conversationId", "conv"),
                "workflowId", mdcOrGenerated("workflowId", "workflow"),
                "userId", userId);
    }

            private Map<String, String> promptTags(String userId, String domain) {
            return Map.of(
                "promptVersion", "v5.0",
                "expectedDomain", domain,
                "traceId", mdcOrGenerated("traceId", "trace"),
                "requestId", mdcOrGenerated("requestId", "req"),
                "conversationId", mdcOrGenerated("conversationId", "conv"),
                "workflowId", mdcOrGenerated("workflowId", "workflow"),
                "userId", userId);
            }

            private Map<String, String> memoryTags(String userId, Chapter13BaselineAssistantService.ConversationEvaluation memory) {
            return Map.of(
                "semanticMemories", String.valueOf(memory.transcript().size()),
                "contextRetention", String.valueOf(memory.metrics().contextRetention()),
                "traceId", mdcOrGenerated("traceId", "trace"),
                "requestId", mdcOrGenerated("requestId", "req"),
                "conversationId", mdcOrGenerated("conversationId", "conv"),
                "workflowId", mdcOrGenerated("workflowId", "workflow"),
                "userId", userId);
            }

            private Map<String, String> ragTags(String userId, Chapter13BaselineAssistantService.RagEvaluation rag) {
            return Map.of(
                "expectedDomain", rag.expectedDomain(),
                "retrievedDocuments", rag.retrievedDocuments().stream().map(Chapter13BaselineAssistantService.RetrievedDocument::documentId).collect(java.util.stream.Collectors.joining(",")),
                "groundedness", String.valueOf(rag.metrics().groundedness()),
                "traceId", mdcOrGenerated("traceId", "trace"),
                "requestId", mdcOrGenerated("requestId", "req"),
                "conversationId", mdcOrGenerated("conversationId", "conv"),
                "workflowId", mdcOrGenerated("workflowId", "workflow"),
                "userId", userId);
            }

            private Map<String, String> toolTags(String userId, Chapter13BaselineAssistantService.ToolCallingSuite tools) {
            Chapter13BaselineAssistantService.ToolCase expenseTool = tools.cases().stream()
                .filter(currentCase -> "createExpense".equals(currentCase.selectedTool()))
                .findFirst()
                .orElse(tools.cases().getFirst());
            return Map.of(
                "tool", expenseTool.selectedTool(),
                "authorized", String.valueOf(expenseTool.authorized()),
                "outcome", expenseTool.outcome(),
                "traceId", mdcOrGenerated("traceId", "trace"),
                "requestId", mdcOrGenerated("requestId", "req"),
                "conversationId", mdcOrGenerated("conversationId", "conv"),
                "workflowId", mdcOrGenerated("workflowId", "workflow"),
                "userId", userId);
            }

            private Map<String, String> mcpTags(String userId, Chapter13BaselineAssistantService.McpSuite mcp, String server) {
            return Map.of(
                "server", server,
                "gracefulFailureRate", String.valueOf(mcp.metrics().gracefulFailureRate()),
                "timeoutHandlingRate", String.valueOf(mcp.metrics().timeoutHandlingRate()),
                "traceId", mdcOrGenerated("traceId", "trace"),
                "requestId", mdcOrGenerated("requestId", "req"),
                "conversationId", mdcOrGenerated("conversationId", "conv"),
                "workflowId", mdcOrGenerated("workflowId", "workflow"),
                "userId", userId);
            }

            private Map<String, String> agentTags(String userId, String agent) {
            return Map.of(
                "agent", agent,
                "traceId", mdcOrGenerated("traceId", "trace"),
                "requestId", mdcOrGenerated("requestId", "req"),
                "conversationId", mdcOrGenerated("conversationId", "conv"),
                "workflowId", mdcOrGenerated("workflowId", "workflow"),
                "userId", userId);
            }

    private String mdcOrGenerated(String key, String prefix) {
        String value = MDC.get(key);
        return value == null || value.isBlank() ? prefix + "-" + UUID.randomUUID() : value;
    }

    private TraceSpan span(
            String spanId,
            String parentSpanId,
            String name,
            String component,
            long latencyMs,
            String status,
            Map<String, String> tags) {
        return new TraceSpan(spanId, parentSpanId, name, component, latencyMs, status, tags);
    }
}