package com.example.chapter13.controller;

import com.example.chapter13.dto.EvaluationResponses.AiMetricsResponse;
import com.example.chapter13.dto.EvaluationResponses.EvaluationCatalogResponse;
import com.example.chapter13.dto.EvaluationResponses.FullEvaluationResult;
import com.example.chapter13.dto.EvaluationResponses.PromptEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.RagEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.WorkflowEvaluationResponse;
import com.example.chapter13.service.EvaluationOrchestratorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class EvaluationsController {

    private final EvaluationOrchestratorService orchestratorService;

    public EvaluationsController(EvaluationOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @GetMapping("/evaluations")
    public EvaluationCatalogResponse evaluations() {
        return orchestratorService.catalog();
    }

    @PostMapping("/evaluations/prompts")
    public PromptEvaluationResponse evaluatePrompts() {
        return orchestratorService.runPromptEvaluation();
    }

    @PostMapping("/evaluations/rag")
    public RagEvaluationResponse evaluateRag() {
        return orchestratorService.runRagEvaluation();
    }

    @PostMapping("/evaluations/workflows")
    public WorkflowEvaluationResponse evaluateWorkflows() {
        return orchestratorService.runWorkflowEvaluation();
    }

    @GetMapping("/evaluations/results")
    public FullEvaluationResult latestResults() {
        return orchestratorService.latestOrRun();
    }

    @GetMapping("/metrics/ai")
    public AiMetricsResponse metrics() {
        return orchestratorService.latestMetrics();
    }
}
