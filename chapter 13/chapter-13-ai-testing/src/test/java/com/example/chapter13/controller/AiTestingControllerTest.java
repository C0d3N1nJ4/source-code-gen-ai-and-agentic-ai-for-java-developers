package com.example.chapter13.controller;

import com.example.chapter13.dto.EvaluationResponses.ConversationEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.ConversationMetrics;
import com.example.chapter13.dto.EvaluationResponses.PipelineReportResponse;
import com.example.chapter13.dto.EvaluationResponses.PromptEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.QualityGateResult;
import com.example.chapter13.dto.EvaluationResponses.RagEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.RetrievalMetrics;
import com.example.chapter13.dto.EvaluationResponses.RetrievedDocument;
import com.example.chapter13.dto.EvaluationResponses.ScenarioResult;
import com.example.chapter13.dto.EvaluationResponses.VersionScore;
import com.example.chapter13.dto.EvaluationResponses.WorkflowCaseResult;
import com.example.chapter13.dto.EvaluationResponses.WorkflowEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.WorkflowMetrics;
import com.example.chapter13.service.ConversationEvaluationService;
import com.example.chapter13.service.EvaluationPipelineService;
import com.example.chapter13.service.PromptEvaluationService;
import com.example.chapter13.service.RagEvaluationService;
import com.example.chapter13.service.WorkflowEvaluationService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiTestingController.class)
@DisplayName("AiTestingController Tests")
class AiTestingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromptEvaluationService promptEvaluationService;

    @MockitoBean
    private RagEvaluationService ragEvaluationService;

    @MockitoBean
    private WorkflowEvaluationService workflowEvaluationService;

    @MockitoBean
    private ConversationEvaluationService conversationEvaluationService;

    @MockitoBean
    private EvaluationPipelineService evaluationPipelineService;

    @Test
    void shouldReturnHealth() throws Exception {
        mockMvc.perform(get("/api/ai-testing/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("chapter-13-ai-testing"));
    }

    @Test
    void shouldEvaluatePrompts() throws Exception {
        when(promptEvaluationService.evaluatePromptVersions(any()))
                .thenReturn(new PromptEvaluationResponse(
                        "candidate",
                        List.of(
                                new VersionScore("baseline", 82, 86, 81, 70, 78, 80, 84, 340, 80, List.of("General framing")),
                                new VersionScore("candidate", 96, 91, 90, 94, 98, 95, 89, 220, 93, List.of("Grounded", "Concise"))),
                        List.of(new ScenarioResult("Explain the travel reimbursement policy.", "candidate", "Grounded response")),
                        Map.of("accuracy", 14)));

        mockMvc.perform(post("/api/ai-testing/prompts/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "baselinePrompt":"You are an enterprise assistant.",
                                  "candidatePrompt":"Use approved documentation and never speculate.",
                                  "scenarios":["Explain the travel reimbursement policy."]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedVersion").value("candidate"));
    }

    @Test
    void shouldEvaluateRag() throws Exception {
        when(ragEvaluationService.evaluateRetrieval(any()))
                .thenReturn(new RagEvaluationResponse(
                        "What is Acme Corporation travel reimbursement policy?",
                        "Finance",
                        List.of(new RetrievedDocument("finance-travel-policy", "Finance", "Travel Reimbursement Policy", "excerpt", 5)),
                        new RetrievalMetrics(95, 100, 100, 100, 99, 5),
                        "Employees may claim transport and meals.",
                        List.of("Grounded retrieval")));

        mockMvc.perform(post("/api/ai-testing/rag/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "question":"What is Acme Corporation travel reimbursement policy?",
                                  "maxDocuments":3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expectedDomain").value("Finance"));
    }

    @Test
    void shouldEvaluateWorkflows() throws Exception {
        when(workflowEvaluationService.evaluateEnterpriseWorkflowSuite())
                .thenReturn(new WorkflowEvaluationResponse(
                        "enterprise-workflow-regression",
                        new WorkflowMetrics(100, 100, 100, 100, 100),
                        List.of(new WorkflowCaseResult("Leave request", "SubmitLeaveRequest", Map.of("date", "next Friday"), "approved", true)),
                        List.of("All workflows passed")));

        mockMvc.perform(get("/api/ai-testing/workflows/evaluate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metrics.toolSelectionAccuracy").value(100));
    }

    @Test
    void shouldEvaluateConversation() throws Exception {
        when(conversationEvaluationService.evaluateConversationBehavior(any()))
                .thenReturn(new ConversationEvaluationResponse(
                        "conv-1301",
                        "EMP-1001",
                        new ConversationMetrics(98, 96, 100, 100, 99),
                        List.of("user: hello", "assistant: hi"),
                        List.of("Context retained")));

        mockMvc.perform(post("/api/ai-testing/conversations/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversationId":"conv-1301",
                                  "userId":"EMP-1001",
                                  "goal":"Continue Project Orion",
                                  "includeIsolationCheck":true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metrics.userIsolation").value(100));
    }

    @Test
    void shouldReturnPipelineReport() throws Exception {
        when(evaluationPipelineService.buildReleaseReport())
                .thenReturn(new PipelineReportResponse(
                        "READY",
                        List.of(new QualityGateResult("Prompt Accuracy", 96, 95, "PASS")),
                        Map.of("promptAggregate", 93),
                        List.of("Release is ready")));

        mockMvc.perform(get("/api/ai-testing/pipeline/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallStatus").value("READY"));
    }
}