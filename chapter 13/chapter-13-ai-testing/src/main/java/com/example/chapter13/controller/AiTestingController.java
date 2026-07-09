package com.example.chapter13.controller;

import com.example.chapter13.dto.EvaluationRequests.ConversationEvaluationRequest;
import com.example.chapter13.dto.EvaluationRequests.PromptEvaluationRequest;
import com.example.chapter13.dto.EvaluationRequests.RagEvaluationRequest;
import com.example.chapter13.dto.EvaluationResponses.ConversationEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.PipelineReportResponse;
import com.example.chapter13.dto.EvaluationResponses.PromptEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.RagEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.WorkflowEvaluationResponse;
import com.example.chapter13.service.ConversationEvaluationService;
import com.example.chapter13.service.EvaluationPipelineService;
import com.example.chapter13.service.PromptEvaluationService;
import com.example.chapter13.service.RagEvaluationService;
import com.example.chapter13.service.WorkflowEvaluationService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai-testing")
public class AiTestingController {

    private final PromptEvaluationService promptEvaluationService;
    private final RagEvaluationService ragEvaluationService;
    private final WorkflowEvaluationService workflowEvaluationService;
    private final ConversationEvaluationService conversationEvaluationService;
    private final EvaluationPipelineService evaluationPipelineService;

    public AiTestingController(
            PromptEvaluationService promptEvaluationService,
            RagEvaluationService ragEvaluationService,
            WorkflowEvaluationService workflowEvaluationService,
            ConversationEvaluationService conversationEvaluationService,
            EvaluationPipelineService evaluationPipelineService) {
        this.promptEvaluationService = promptEvaluationService;
        this.ragEvaluationService = ragEvaluationService;
        this.workflowEvaluationService = workflowEvaluationService;
        this.conversationEvaluationService = conversationEvaluationService;
        this.evaluationPipelineService = evaluationPipelineService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "chapter-13-ai-testing");
    }

    @PostMapping("/prompts/evaluate")
    public PromptEvaluationResponse evaluatePrompts(@Valid @RequestBody PromptEvaluationRequest request) {
        return promptEvaluationService.evaluatePromptVersions(request);
    }

    @PostMapping("/rag/evaluate")
    public RagEvaluationResponse evaluateRag(@Valid @RequestBody RagEvaluationRequest request) {
        return ragEvaluationService.evaluateRetrieval(request);
    }

    @GetMapping("/workflows/evaluate")
    public WorkflowEvaluationResponse evaluateWorkflows() {
        return workflowEvaluationService.evaluateEnterpriseWorkflowSuite();
    }

    @PostMapping("/conversations/evaluate")
    public ConversationEvaluationResponse evaluateConversation(
            @Valid @RequestBody ConversationEvaluationRequest request) {
        return conversationEvaluationService.evaluateConversationBehavior(request);
    }

    @GetMapping("/pipeline/report")
    public PipelineReportResponse pipelineReport() {
        return evaluationPipelineService.buildReleaseReport();
    }
}