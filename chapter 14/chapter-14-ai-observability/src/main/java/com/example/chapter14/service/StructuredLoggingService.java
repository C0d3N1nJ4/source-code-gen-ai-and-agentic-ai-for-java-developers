package com.example.chapter14.service;

import com.example.chapter14.dto.ObservabilityRequests.StructuredLogRequest;
import com.example.chapter14.dto.ObservabilityResponses.StructuredLogEvent;
import com.example.chapter14.dto.ObservabilityResponses.StructuredLogResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class StructuredLoggingService {

        private static final Logger LOGGER = LoggerFactory.getLogger(StructuredLoggingService.class);

        private final Chapter13BaselineAssistantService baselineAssistantService;

        public StructuredLoggingService(Chapter13BaselineAssistantService baselineAssistantService) {
                this.baselineAssistantService = baselineAssistantService;
        }

    public StructuredLogResponse simulate(StructuredLogRequest request) {
                String userId = request.includeSensitiveMetadata() ? "employee-visible" : "[REDACTED]";
                String workflowId = mdcOrGenerated("workflowId", "workflow");
                String requestId = mdcOrGenerated("requestId", "req");
                String model = "llama3.1";
                var prompt = baselineAssistantService.defaultPromptComparison();
                var rag = baselineAssistantService.defaultRagEvaluation();
                var tools = baselineAssistantService.toolCallingSuite();
                var workflow = baselineAssistantService.workflowSuite();
                var conversation = baselineAssistantService.defaultConversationEvaluation();
                Chapter13BaselineAssistantService.ToolCase expenseTool = tools.cases().stream()
                                .filter(currentCase -> "createExpense".equals(currentCase.selectedTool()))
                                .findFirst()
                                .orElse(tools.cases().getFirst());

        List<StructuredLogEvent> events = List.of(
                new StructuredLogEvent(
                        "2026-07-02T12:00:01Z",
                        "INFO",
                        "prompt.constructed",
                        Map.of(
                                "traceId", request.traceId(),
                                "spanId", "span-2",
                                "requestId", requestId,
                                "conversationId", request.conversationId(),
                                "workflowId", workflowId,
                                "promptVersion", request.promptVersion(),
                                "recommendedPrompt", prompt.recommendedVersion(),
                                "model", model,
                                "userId", userId)),
                new StructuredLogEvent(
                        "2026-07-02T12:00:01Z",
                        "INFO",
                        "rag.documents_retrieved",
                        Map.of(
                                "traceId", request.traceId(),
                                "spanId", "span-4",
                                "requestId", requestId,
                                "workflowId", workflowId,
                                "documentIds", rag.retrievedDocuments().stream().map(Chapter13BaselineAssistantService.RetrievedDocument::documentId).collect(java.util.stream.Collectors.joining(",")),
                                "similarityScore", String.valueOf(rag.retrievedDocuments().getFirst().score() / 5.0),
                                "model", model,
                                "userId", userId)),
                new StructuredLogEvent(
                        "2026-07-02T12:00:02Z",
                        "INFO",
                        "tool.executed",
                        Map.of(
                                "traceId", request.traceId(),
                                "spanId", "span-6",
                                "requestId", requestId,
                                "workflowId", workflowId,
                                "toolName", expenseTool.selectedTool(),
                                "status", expenseTool.outcome(),
                                "businessWorkflow", workflow.cases().get(1).scenario(),
                                "model", model,
                                "userId", userId)),
                new StructuredLogEvent(
                        "2026-07-02T12:00:02Z",
                        "WARN",
                        "mcp.timeout",
                        Map.of(
                                "traceId", request.traceId(),
                                "spanId", "span-7",
                                "requestId", requestId,
                                "workflowId", workflowId,
                                "server", "finance-mcp",
                                "recovery", "support-mcp fallback",
                                "memoryContextSize", String.valueOf(conversation.transcript().size()),
                                "model", model,
                                "userId", userId)));

        events.forEach(event -> {
            event.fields().forEach(MDC::put);
            LOGGER.info("eventType={} level={} fields={}", event.eventType(), event.level(), event.fields());
            MDC.clear();
        });

        return new StructuredLogResponse(
                request.traceId(),
                events,
                List.of(
                        "Mask user identifiers unless explicit operational access is required.",
                        "Do not persist raw prompt bodies containing enterprise-sensitive content.",
                        "Exclude authentication tokens, API keys, passwords, and confidential documents from log fields."),
                List.of("traceId", "spanId", "requestId", "conversationId", "workflowId", "userId", "model", "promptVersion"));
    }

    private String mdcOrGenerated(String key, String prefix) {
        String value = MDC.get(key);
        return value == null || value.isBlank() ? prefix + "-" + UUID.randomUUID() : value;
    }
}