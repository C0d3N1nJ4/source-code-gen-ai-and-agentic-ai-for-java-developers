package com.example.chapter13.service;

import com.example.chapter13.dto.EvaluationResponses.AiMetricsResponse;
import com.example.chapter13.dto.EvaluationResponses.EvaluationCatalogResponse;
import com.example.chapter13.dto.EvaluationResponses.FullEvaluationResult;
import com.example.chapter13.dto.EvaluationResponses.PromptEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.RagEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.ToolCallingEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.WorkflowEvaluationResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class EvaluationOrchestratorService {

    private final PromptEvaluationService promptEvaluationService;
    private final RagEvaluationService ragEvaluationService;
    private final ToolCallingEvaluationService toolCallingEvaluationService;
    private final McpEvaluationService mcpEvaluationService;
    private final ConversationEvaluationService conversationEvaluationService;
    private final MultiAgentEvaluationService multiAgentEvaluationService;
    private final WorkflowEvaluationService workflowEvaluationService;
    private final EvaluationPipelineService evaluationPipelineService;
    private final AiMetricsService aiMetricsService;
    private final EvaluationResultStoreService resultStoreService;

    public EvaluationOrchestratorService(
            PromptEvaluationService promptEvaluationService,
            RagEvaluationService ragEvaluationService,
            ToolCallingEvaluationService toolCallingEvaluationService,
            McpEvaluationService mcpEvaluationService,
            ConversationEvaluationService conversationEvaluationService,
            MultiAgentEvaluationService multiAgentEvaluationService,
            WorkflowEvaluationService workflowEvaluationService,
            EvaluationPipelineService evaluationPipelineService,
            AiMetricsService aiMetricsService,
            EvaluationResultStoreService resultStoreService) {
        this.promptEvaluationService = promptEvaluationService;
        this.ragEvaluationService = ragEvaluationService;
        this.toolCallingEvaluationService = toolCallingEvaluationService;
        this.mcpEvaluationService = mcpEvaluationService;
        this.conversationEvaluationService = conversationEvaluationService;
        this.multiAgentEvaluationService = multiAgentEvaluationService;
        this.workflowEvaluationService = workflowEvaluationService;
        this.evaluationPipelineService = evaluationPipelineService;
        this.aiMetricsService = aiMetricsService;
        this.resultStoreService = resultStoreService;
    }

    public EvaluationCatalogResponse catalog() {
        FullEvaluationResult latest = resultStoreService.readLatest();
        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("prompts", "POST /evaluations/prompts");
        endpoints.put("rag", "POST /evaluations/rag");
        endpoints.put("workflows", "POST /evaluations/workflows");
        endpoints.put("results", "GET /evaluations/results");
        endpoints.put("metrics", "GET /metrics/ai");

        return new EvaluationCatalogResponse(
                "Acme Enterprise Assistant",
                List.of(
                        "Unit Tests",
                        "Integration Tests",
                        "Prompt Evaluation",
                        "RAG Evaluation",
                        "Tool Calling Tests",
                        "MCP Tests",
                        "Memory Tests",
                        "Multi-Agent Tests",
                        "Workflow Tests"),
                endpoints,
                List.of(
                        "datasets/prompts.json",
                        "datasets/rag.json",
                        "datasets/tool-calling.json",
                        "datasets/memory.json",
                        "datasets/agents.json",
                        "datasets/workflows.json"),
                latest == null ? "none" : latest.resultId());
    }

    public PromptEvaluationResponse runPromptEvaluation() {
        return promptEvaluationService.defaultComparison();
    }

    public RagEvaluationResponse runRagEvaluation() {
        return ragEvaluationService.defaultEvaluation();
    }

    public WorkflowEvaluationResponse runWorkflowEvaluation() {
        return workflowEvaluationService.evaluateEnterpriseWorkflowSuite();
    }

    public FullEvaluationResult runFullSuite() {
        PromptEvaluationResponse prompt = promptEvaluationService.defaultComparison();
        RagEvaluationResponse rag = ragEvaluationService.defaultEvaluation();
        ToolCallingEvaluationResponse tool = toolCallingEvaluationService.evaluateToolCallingSuite();
        var mcp = mcpEvaluationService.evaluateMcpSuite();
        var memory = conversationEvaluationService.defaultEvaluation();
        var agents = multiAgentEvaluationService.evaluateAgentSuite();
        WorkflowEvaluationResponse workflows = workflowEvaluationService.evaluateEnterpriseWorkflowSuite();
        var qualityReport = evaluationPipelineService.buildReleaseReport();

        FullEvaluationResult partial = new FullEvaluationResult(
                UUID.randomUUID().toString(),
                prompt,
                rag,
                tool,
                mcp,
                memory,
                agents,
                workflows,
                qualityReport,
                null);
        AiMetricsResponse metrics = aiMetricsService.from(partial);
        FullEvaluationResult full = new FullEvaluationResult(
                partial.resultId(),
                prompt,
                rag,
                tool,
                mcp,
                memory,
                agents,
                workflows,
                qualityReport,
                metrics);

        resultStoreService.persist(full);
        return full;
    }

    public FullEvaluationResult latestOrRun() {
        FullEvaluationResult latest = resultStoreService.readLatest();
        return latest != null ? latest : runFullSuite();
    }

    public AiMetricsResponse latestMetrics() {
        return latestOrRun().aiMetrics();
    }
}
