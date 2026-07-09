package com.example.chapter14.dto;

import java.util.List;
import java.util.Map;

public final class ObservabilityResponses {

    private ObservabilityResponses() {
    }

    public record TraceSimulationResponse(
            String traceId,
            String requestId,
            String conversationId,
            String workflowId,
            String userId,
            String workflowName,
            List<TraceSpanView> spans,
            Map<String, Long> componentLatencyMs,
            List<String> participatingAgents,
            List<String> contactedMcpServers) {
    }

    public record TraceSpanView(
            String spanId,
            String parentSpanId,
            String name,
            String component,
            long latencyMs,
            String status,
            Map<String, String> tags) {
    }

    public record MetricsReportResponse(
            String platformName,
            ModelMetrics modelMetrics,
            TokenMetrics tokenMetrics,
            RagMetrics ragMetrics,
            ToolMetrics toolMetrics,
            McpMetrics mcpMetrics,
            AgentMetrics agentMetrics,
            CostMetrics costMetrics,
            List<String> observations) {
    }

    public record ModelMetrics(
            Map<String, Integer> requestsByModel,
            int promptExecutionTimeMs,
            double averageResponseLatencyMs) {
    }

    public record TokenMetrics(
            int inputTokens,
            int outputTokens,
            int totalTokens,
            int dailyBudgetUsedPercent) {
    }

    public record RagMetrics(
            int retrievedDocuments,
            double retrievalLatencyMs,
            double averageSimilarityScore,
            int retrievalSuccessRate) {
    }

    public record ToolMetrics(
            Map<String, Integer> invocationCount,
            int executionSuccessRate,
            double averageExecutionTimeMs) {
    }

    public record McpMetrics(
            Map<String, Double> serverLatencyMs,
            int availabilityPercent,
            int failureRatePercent) {
    }

    public record AgentMetrics(
            Map<String, Integer> selectionFrequency,
            double workflowDurationMs,
            int imbalanceRiskPercent) {
    }

    public record CostMetrics(
            double estimatedDailyCost,
            double costPerConversation,
            String primaryCostDriver) {
    }

    public record StructuredLogResponse(
            String traceId,
            List<StructuredLogEvent> events,
            List<String> redactionPolicies,
            List<String> correlationFields) {
    }

    public record StructuredLogEvent(
            String timestamp,
            String level,
            String eventType,
            Map<String, String> fields) {
    }

    public record DashboardSummaryResponse(
            String dashboardName,
            List<DashboardPanel> panels,
            List<AlertView> alerts,
            List<String> businessSignals) {
    }

    public record DashboardPanel(
            String title,
            String primaryMetric,
            String status,
            String insight) {
    }

    public record AlertView(
            String name,
            String trigger,
            String severity,
            String firstInvestigationStep) {
    }

    public record ImprovementRecommendationsResponse(
            List<ImprovementOpportunity> opportunities,
            List<String> telemetrySignals) {
    }

    public record ImprovementOpportunity(
            String area,
            String evidence,
            String action,
            String expectedBenefit) {
    }

    public record ObservabilityArchitectureResponse(
            String platformName,
            List<ObservabilityPillar> pillars,
            List<String> telemetrySources,
            List<String> stakeholders,
            List<String> architecturePrinciples) {
    }

    public record AiMetricsApiResponse(
            String platformName,
            ModelMetrics modelMetrics,
            TokenMetrics tokenMetrics,
            CostMetrics costMetrics,
            double responseLatencyMs) {
    }

    public record RagMetricsApiResponse(
            RagMetrics ragMetrics,
            int groundedResponses,
            int hallucinationCount,
            double groundingScore) {
    }

    public record ToolMetricsApiResponse(
            ToolMetrics toolMetrics,
            int retries,
            int authorizationFailures) {
    }

    public record McpMetricsApiResponse(
            McpMetrics mcpMetrics,
            int timeoutCount,
            int retryCount,
            int authenticationFailures,
            int authorizationFailures) {
    }

    public record AgentMetricsApiResponse(
            AgentMetrics agentMetrics,
            double planningDurationMs,
            double delegationLatencyMs,
            int successRate) {
    }

    public record WorkflowMetricsApiResponse(
            String workflowName,
            int businessTransactions,
            int completionRate,
            double workflowDurationMs,
            Map<String, Integer> businessOperations) {
    }

    public record TokenMetricsApiResponse(
            TokenMetrics tokenMetrics,
            Map<String, Integer> tokensPerUser,
            Map<String, Integer> tokensPerWorkflow,
            Map<String, Integer> tokensPerModel) {
    }

    public record BusinessMetricsApiResponse(
            Map<String, Integer> businessOperations,
            int workflowSuccessRate,
            double approvalLatencyMs,
            double userActivityScore) {
    }

    public record ObservabilityPillar(
            String name,
            String purpose,
            List<String> signals) {
    }
}