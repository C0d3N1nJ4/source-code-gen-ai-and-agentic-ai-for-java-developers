package com.example.chapter14.service;

import com.example.chapter14.config.ObservabilityCostProperties;
import com.example.chapter14.config.ObservabilityThresholdProperties;
import com.example.chapter14.dto.ObservabilityResponses.AgentMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.AiMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.AgentMetrics;
import com.example.chapter14.dto.ObservabilityResponses.BusinessMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.CostMetrics;
import com.example.chapter14.dto.ObservabilityResponses.McpMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.McpMetrics;
import com.example.chapter14.dto.ObservabilityResponses.MetricsReportResponse;
import com.example.chapter14.dto.ObservabilityResponses.ModelMetrics;
import com.example.chapter14.dto.ObservabilityResponses.RagMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.RagMetrics;
import com.example.chapter14.dto.ObservabilityResponses.TokenMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.TokenMetrics;
import com.example.chapter14.dto.ObservabilityResponses.ToolMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.ToolMetrics;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class MetricsObservationService {

    private final MeterRegistry meterRegistry;
    private final ObservabilityCostProperties costProperties;
    private final ObservabilityThresholdProperties thresholdProperties;
    private final Chapter13BaselineAssistantService baselineAssistantService;
    private final AssistantRuntimeService assistantRuntimeService;

    public MetricsObservationService(
        MeterRegistry meterRegistry,
        ObservabilityCostProperties costProperties,
        ObservabilityThresholdProperties thresholdProperties,
        Chapter13BaselineAssistantService baselineAssistantService,
        AssistantRuntimeService assistantRuntimeService) {
        this.meterRegistry = meterRegistry;
        this.costProperties = costProperties;
        this.thresholdProperties = thresholdProperties;
        this.baselineAssistantService = baselineAssistantService;
        this.assistantRuntimeService = assistantRuntimeService;
        recordMeters(buildReport());
    }

    public MetricsReportResponse report() {
        return buildReport();
    }

        private MetricsReportResponse buildReport() {
        var prompt = baselineAssistantService.defaultPromptComparison();
        var rag = baselineAssistantService.defaultRagEvaluation();
        var toolCalling = baselineAssistantService.toolCallingSuite();
        var mcp = baselineAssistantService.mcpSuite();
        var agents = baselineAssistantService.agentSuite();
        var workflow = baselineAssistantService.workflowSuite();
        var conversation = baselineAssistantService.defaultConversationEvaluation();

        TokenMetrics tokenMetrics = new TokenMetrics(420, 180, 600, 63);

        return new MetricsReportResponse(
            "Acme Corporation Enterprise Assistant",
            new ModelMetrics(
                Map.of("llama3.1", 178, "gpt-4.1-mini", 24),
                Math.max(20, prompt.candidate().aggregateScore() / 3),
                rag.metrics().retrievalLatencyMs()
                    + toolCalling.metrics().averageToolLatencyMs()
                    + mcp.metrics().averageMcpLatencyMs()
                    + agents.metrics().averageWorkflowDurationMs()),
            tokenMetrics,
            new RagMetrics(
                rag.retrievedDocuments().size(),
                rag.metrics().retrievalLatencyMs(),
                rag.retrievedDocuments().isEmpty() ? 0.0 : rag.retrievedDocuments().getFirst().score() / 5.0,
                rag.metrics().groundedness()),
            new ToolMetrics(
                toToolCounts(toolCalling.cases()),
                toolCalling.metrics().businessRuleExecution(),
                toolCalling.metrics().averageToolLatencyMs()),
            new McpMetrics(
                Map.of(
                    "finance-mcp", (double) mcp.metrics().averageMcpLatencyMs(),
                    "support-mcp", 84.0,
                    "engineering-mcp", 39.0),
                mcp.metrics().authorizationRate(),
                Math.max(1, 100 - mcp.metrics().gracefulFailureRate() + 1)),
            new AgentMetrics(
                toAgentCounts(agents.cases()),
                agents.metrics().averageWorkflowDurationMs(),
                34),
            new CostMetrics(
                estimatedDailyCost(tokenMetrics.inputTokens(), tokenMetrics.outputTokens(), 202),
                estimatedConversationCost(tokenMetrics.inputTokens(), tokenMetrics.outputTokens()),
                "token consumption"),
            List.of(
                "Token usage remains below the current daily budget threshold of " + thresholdProperties.dailyTokenBudget() + ".",
                "Finance MCP latency remains the dominant contributor to end-to-end workflow delay.",
                "Knowledge retrieval remains strong with a groundedness score of " + rag.metrics().groundedness() + ".",
                "Conversation memory retains Project Orion and Amsterdam context across turns.",
                "Workflow observations continue from chapter 13 scenarios: " + workflow.observations().getFirst()));
        }

    public AiMetricsApiResponse aiMetrics() {
    MetricsReportResponse report = report();
    return new AiMetricsApiResponse(
        report.platformName(),
        report.modelMetrics(),
        report.tokenMetrics(),
        report.costMetrics(),
        report.modelMetrics().averageResponseLatencyMs());
    }

    public RagMetricsApiResponse ragMetrics() {
    MetricsReportResponse report = report();
    int groundedResponses = (int) Math.round(report.ragMetrics().retrievalSuccessRate() * 0.95);
    int hallucinationCount = Math.max(0, 100 - groundedResponses);
    return new RagMetricsApiResponse(report.ragMetrics(), groundedResponses, hallucinationCount, 0.93);
    }

    public ToolMetricsApiResponse toolMetrics() {
    MetricsReportResponse report = report();
    return new ToolMetricsApiResponse(report.toolMetrics(), 3, 1);
    }

    public McpMetricsApiResponse mcpMetrics() {
    MetricsReportResponse report = report();
    return new McpMetricsApiResponse(report.mcpMetrics(), 2, 3, 0, 1);
    }

    public AgentMetricsApiResponse agentMetrics() {
    MetricsReportResponse report = report();
    return new AgentMetricsApiResponse(report.agentMetrics(), 71.0, 53.0, 99);
    }

    public com.example.chapter14.dto.ObservabilityResponses.WorkflowMetricsApiResponse workflowMetrics() {
    MetricsReportResponse report = report();
    return new com.example.chapter14.dto.ObservabilityResponses.WorkflowMetricsApiResponse(
        "travel-request-enterprise-workflow",
        214,
        99,
        report.agentMetrics().workflowDurationMs(),
        Map.of(
            "leaveRequests", 1240,
            "expenseClaims", 870,
            "knowledgeSearches", 8100,
            "incidentWorkflows", 19,
            "travelRequests", 512,
            "approvals", 1398));
    }

    public TokenMetricsApiResponse tokenMetrics() {
    MetricsReportResponse report = report();
    return new TokenMetricsApiResponse(
        report.tokenMetrics(),
        Map.of("EMP-1001", 8300, "EMP-2042", 6510),
        Map.of("travel-request-enterprise-workflow", 3290, "incident-escalation-workflow", 1410),
        Map.of("llama3.1", 562000, "gpt-4.1-mini", 114000));
    }

    public BusinessMetricsApiResponse businessMetrics() {
        var workflowMetrics = workflowMetrics();
        return new BusinessMetricsApiResponse(
                workflowMetrics.businessOperations(),
                workflowMetrics.completionRate(),
                320.0,
                92.0);
    }

    private void recordMeters(MetricsReportResponse report) {
    Timer.builder("ai.prompt.duration")
        .description("Prompt construction duration")
        .register(meterRegistry)
        .record(report.modelMetrics().promptExecutionTimeMs(), TimeUnit.MILLISECONDS);

    Counter.builder("ai.tokens.input")
        .description("Input token count")
        .register(meterRegistry)
        .increment(report.tokenMetrics().inputTokens());
    Counter.builder("ai.tokens.output")
        .description("Output token count")
        .register(meterRegistry)
        .increment(report.tokenMetrics().outputTokens());
    Counter.builder("ai.tokens.total")
        .description("Total token count")
        .register(meterRegistry)
        .increment(report.tokenMetrics().totalTokens());

    Gauge.builder("ai.rag.similarity.score", report.ragMetrics(), RagMetrics::averageSimilarityScore)
        .description("Average RAG similarity score")
        .register(meterRegistry);
    Gauge.builder("ai.rag.documents.retrieved", report.ragMetrics(), RagMetrics::retrievedDocuments)
        .description("Retrieved document count")
        .register(meterRegistry);

    Timer.builder("ai.vector.search.duration")
        .description("Vector search duration")
        .register(meterRegistry)
        .record((long) report.ragMetrics().retrievalLatencyMs(), TimeUnit.MILLISECONDS);

    Counter.builder("ai.tools.invocations")
        .description("Tool invocation count")
        .tag("tool", "estimateExpense")
        .register(meterRegistry)
        .increment(82);
    Counter.builder("ai.tools.failures")
        .description("Tool failure count")
        .tag("tool", "restartServer")
        .register(meterRegistry)
        .increment(1);

    Gauge.builder("ai.mcp.failure.rate", report.mcpMetrics(), McpMetrics::failureRatePercent)
        .description("MCP failure rate")
        .register(meterRegistry);
    Gauge.builder("ai.response.latency", report.modelMetrics(), ModelMetrics::averageResponseLatencyMs)
        .description("Average AI response latency")
        .register(meterRegistry);
    Gauge.builder("ai.workflow.duration", report.agentMetrics(), AgentMetrics::workflowDurationMs)
        .description("AI workflow duration")
        .register(meterRegistry);

    report.agentMetrics().selectionFrequency().forEach((agent, count) -> Counter.builder("ai.agent.executions")
        .tag("agent", agent)
        .description("Agent execution count")
        .register(meterRegistry)
        .increment(count));
    }

    private double estimatedConversationCost(int inputTokens, int outputTokens) {
    return inputTokens * costProperties.inputTokenUnitCost()
        + outputTokens * costProperties.outputTokenUnitCost();
    }

    private double estimatedDailyCost(int inputTokens, int outputTokens, int conversations) {
    return estimatedConversationCost(inputTokens, outputTokens) * conversations;
    }

    private Map<String, Integer> toToolCounts(List<Chapter13BaselineAssistantService.ToolCase> cases) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Chapter13BaselineAssistantService.ToolCase currentCase : cases) {
            counts.merge(currentCase.selectedTool(), 1, Integer::sum);
        }
        return counts;
    }

    private Map<String, Integer> toAgentCounts(List<Chapter13BaselineAssistantService.AgentCase> cases) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Chapter13BaselineAssistantService.AgentCase currentCase : cases) {
            for (String agent : currentCase.routedAgents()) {
                counts.merge(agent, 1, Integer::sum);
            }
        }
        return counts;
    }
}