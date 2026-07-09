package com.example.chapter13.service;

import com.example.chapter13.config.AiQualityProperties;
import com.example.chapter13.dto.EvaluationResponses.ConversationEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.PipelineReportResponse;
import com.example.chapter13.dto.EvaluationResponses.PromptEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.QualityGateResult;
import com.example.chapter13.dto.EvaluationResponses.RagEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.ToolCallingEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.VersionScore;
import com.example.chapter13.dto.EvaluationResponses.WorkflowEvaluationResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class EvaluationPipelineService {

    private final PromptEvaluationService promptEvaluationService;
    private final RagEvaluationService ragEvaluationService;
    private final WorkflowEvaluationService workflowEvaluationService;
    private final ConversationEvaluationService conversationEvaluationService;
    private final ToolCallingEvaluationService toolCallingEvaluationService;
    private final AiQualityProperties qualityProperties;

    public EvaluationPipelineService(
            PromptEvaluationService promptEvaluationService,
            RagEvaluationService ragEvaluationService,
            WorkflowEvaluationService workflowEvaluationService,
            ConversationEvaluationService conversationEvaluationService,
            ToolCallingEvaluationService toolCallingEvaluationService,
            AiQualityProperties qualityProperties) {
        this.promptEvaluationService = promptEvaluationService;
        this.ragEvaluationService = ragEvaluationService;
        this.workflowEvaluationService = workflowEvaluationService;
        this.conversationEvaluationService = conversationEvaluationService;
        this.toolCallingEvaluationService = toolCallingEvaluationService;
        this.qualityProperties = qualityProperties;
    }

    public PipelineReportResponse buildReleaseReport() {
        PromptEvaluationResponse promptReport = promptEvaluationService.defaultComparison();
        VersionScore candidate = promptReport.versions().stream()
                .filter(version -> "candidate".equals(version.version()))
                .findFirst()
                .orElseThrow();
        RagEvaluationResponse ragReport = ragEvaluationService.defaultEvaluation();
        ToolCallingEvaluationResponse toolReport = toolCallingEvaluationService.evaluateToolCallingSuite();
        WorkflowEvaluationResponse workflowReport = workflowEvaluationService.evaluateEnterpriseWorkflowSuite();
        ConversationEvaluationResponse conversationReport = conversationEvaluationService.defaultEvaluation();
        int hallucinationRate = Math.max(0, 100 - ragReport.metrics().groundedness());

        List<QualityGateResult> gates = List.of(
            gate("Prompt Accuracy", candidate.accuracy(), qualityProperties.promptAccuracyThreshold()),
            gate("Grounded Responses", ragReport.metrics().groundedness(), qualityProperties.groundedResponsesThreshold()),
            gate("Tool Selection Accuracy", toolReport.metrics().toolSelectionAccuracy(), qualityProperties.toolSelectionThreshold()),
            gate("Workflow Completion", workflowReport.metrics().workflowCompletionRate(), qualityProperties.workflowCompletionThreshold()),
            reverseGate("Unauthorized Tool Calls", toolReport.metrics().unauthorizedToolCalls(), qualityProperties.unauthorizedToolCallsThreshold()),
            reverseGate("Hallucination Rate", hallucinationRate, qualityProperties.hallucinationRateMaxThreshold()),
            gate("User Isolation", conversationReport.metrics().userIsolation(), 100));

        boolean passed = gates.stream().allMatch(gate -> "PASS".equals(gate.status()));
        Map<String, Integer> headlineMetrics = new LinkedHashMap<>();
        headlineMetrics.put("promptAggregate", candidate.aggregateScore());
        headlineMetrics.put("ragGroundedness", ragReport.metrics().groundedness());
        headlineMetrics.put("hallucinationRate", hallucinationRate);
        headlineMetrics.put("workflowCompletion", workflowReport.metrics().workflowCompletionRate());
        headlineMetrics.put("toolSelection", toolReport.metrics().toolSelectionAccuracy());
        headlineMetrics.put("memoryIsolation", conversationReport.metrics().userIsolation());

        List<String> releaseNotes = List.of(
                "Prompt comparison shows the grounded candidate prompt outperforming the baseline.",
                "RAG retrieval remains strongly aligned to finance knowledge for travel-policy questions.",
                "Tool calling and workflow suites indicate the assistant is ready for production-style regression enforcement.");

        return new PipelineReportResponse(passed ? "READY" : "BLOCKED", gates, headlineMetrics, releaseNotes);
    }

    private QualityGateResult gate(String gate, int actual, int threshold) {
        return new QualityGateResult(gate, actual, threshold, actual >= threshold ? "PASS" : "FAIL");
    }

    private QualityGateResult reverseGate(String gate, int actual, int threshold) {
        return new QualityGateResult(gate, actual, threshold, actual <= threshold ? "PASS" : "FAIL");
    }
}