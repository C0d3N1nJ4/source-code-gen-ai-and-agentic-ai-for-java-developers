package com.example.chapter13.dto;

import java.util.List;
import java.util.Map;

public final class EvaluationResponses {

    private EvaluationResponses() {
    }

    public record PromptEvaluationResponse(
            String recommendedVersion,
            List<VersionScore> versions,
            List<ScenarioResult> scenarios,
            Map<String, Integer> qualityDelta) {
    }

    public record VersionScore(
            String version,
            int accuracy,
            int relevance,
            int completeness,
            int conciseness,
            int groundedness,
            int safety,
            int tone,
            int averageResponseLength,
            int aggregateScore,
            List<String> strengths) {
    }

    public record ScenarioResult(
            String scenario,
            String winningVersion,
            String reason) {
    }

    public record RagEvaluationResponse(
            String question,
            String expectedDomain,
            List<RetrievedDocument> retrievedDocuments,
            RetrievalMetrics metrics,
            String generatedAnswer,
            List<String> findings) {
    }

    public record RetrievedDocument(
            String documentId,
            String domain,
            String title,
            String excerpt,
            int score) {
    }

    public record RetrievalMetrics(
            int relevance,
            int precision,
            int recall,
            int rankingQuality,
            int groundedness,
            long retrievalLatencyMs) {
    }

    public record WorkflowEvaluationResponse(
            String suiteName,
            WorkflowMetrics metrics,
            List<WorkflowCaseResult> cases,
            List<String> observations) {
    }

    public record WorkflowMetrics(
            int toolSelectionAccuracy,
            int parameterAccuracy,
            int businessValidationRate,
            int gracefulFailureRate,
            int workflowCompletionRate) {
    }

    public record WorkflowCaseResult(
            String scenario,
            String selectedTool,
            Map<String, String> parameters,
            String outcome,
            boolean passed) {
    }

    public record ToolCallingEvaluationResponse(
            String suiteName,
            ToolCallingMetrics metrics,
            List<ToolCaseResult> cases,
            List<String> observations) {
    }

    public record ToolCallingMetrics(
            int toolSelectionAccuracy,
            int parameterExtractionAccuracy,
            int authorizationEnforcement,
            int businessRuleExecution,
            int failureHandlingRate,
            int unauthorizedToolCalls,
            long averageToolLatencyMs) {
    }

    public record ToolCaseResult(
            String scenario,
            String selectedTool,
            Map<String, String> parameters,
            boolean authorized,
            String outcome,
            boolean passed) {
    }

    public record McpEvaluationResponse(
            String suiteName,
            McpMetrics metrics,
            List<McpCaseResult> cases,
            List<String> observations) {
    }

    public record McpMetrics(
            int discoverySuccessRate,
            int authenticationRate,
            int authorizationRate,
            int capabilityLookupRate,
            int serializationRate,
            int retryHandlingRate,
            int timeoutHandlingRate,
            int gracefulFailureRate,
            long averageMcpLatencyMs) {
    }

    public record McpCaseResult(
            String capability,
            boolean authenticated,
            boolean authorized,
            String outcome,
            boolean passed) {
    }

    public record AgentEvaluationResponse(
            String suiteName,
            AgentMetrics metrics,
            List<AgentCaseResult> cases,
            List<String> observations) {
    }

    public record AgentMetrics(
            int planningAccuracy,
            int routingAccuracy,
            int delegationAccuracy,
            int aggregationQuality,
            int partialFailureHandling,
            int agentIsolation,
            long averageWorkflowDurationMs) {
    }

    public record AgentCaseResult(
            String scenario,
            List<String> routedAgents,
            String outcome,
            boolean passed) {
    }

    public record ConversationEvaluationResponse(
            String conversationId,
            String userId,
            ConversationMetrics metrics,
            List<String> transcript,
            List<String> observations) {
    }

    public record ConversationMetrics(
            int contextRetention,
            int contextBoundaryRespect,
            int preferenceReuse,
            int userIsolation,
            int workflowCompletion) {
    }

    public record PipelineReportResponse(
            String overallStatus,
            List<QualityGateResult> gates,
            Map<String, Integer> headlineMetrics,
            List<String> releaseNotes) {
    }

    public record EvaluationCatalogResponse(
            String project,
            List<String> layers,
            Map<String, String> endpoints,
            List<String> datasets,
            String latestResultId) {
    }

    public record AiMetricsResponse(
            String resultId,
            Map<String, Integer> quality,
            Map<String, Long> latencyMs,
            Map<String, Integer> usage) {
    }

    public record FullEvaluationResult(
            String resultId,
            PromptEvaluationResponse prompt,
            RagEvaluationResponse rag,
            ToolCallingEvaluationResponse toolCalling,
            McpEvaluationResponse mcp,
            ConversationEvaluationResponse memory,
            AgentEvaluationResponse agents,
            WorkflowEvaluationResponse workflows,
            PipelineReportResponse qualityReport,
            AiMetricsResponse aiMetrics) {
    }

    public record QualityGateResult(
            String gate,
            int actual,
            int threshold,
            String status) {
    }
}