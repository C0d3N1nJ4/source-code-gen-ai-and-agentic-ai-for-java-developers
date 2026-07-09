package com.example.chapter13.service;

import com.example.chapter13.dto.EvaluationResponses.AiMetricsResponse;
import com.example.chapter13.dto.EvaluationResponses.FullEvaluationResult;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AiMetricsService {

    public AiMetricsResponse from(FullEvaluationResult result) {
        Map<String, Integer> quality = new LinkedHashMap<>();
        quality.put("promptAccuracy", result.prompt().versions().stream()
                .filter(score -> "candidate".equals(score.version()))
                .findFirst()
                .map(score -> score.accuracy())
                .orElse(0));
        quality.put("groundedResponses", result.rag().metrics().groundedness());
        quality.put("hallucinationRate", 100 - result.rag().metrics().groundedness());
        quality.put("toolSelectionAccuracy", result.toolCalling().metrics().toolSelectionAccuracy());
        quality.put("workflowCompletionRate", result.workflows().metrics().workflowCompletionRate());
        quality.put("retrievalPrecision", result.rag().metrics().precision());
        quality.put("retrievalRecall", result.rag().metrics().recall());

        Map<String, Long> latency = new LinkedHashMap<>();
        latency.put("promptExecutionMs", 34L);
        latency.put("retrievalLatencyMs", result.rag().metrics().retrievalLatencyMs());
        latency.put("toolExecutionMs", result.toolCalling().metrics().averageToolLatencyMs());
        latency.put("mcpLatencyMs", result.mcp().metrics().averageMcpLatencyMs());
        latency.put("workflowDurationMs", result.agents().metrics().averageWorkflowDurationMs());

        Map<String, Integer> usage = new LinkedHashMap<>();
        usage.put("tokenUsage", 1142);
        usage.put("promptTokens", 628);
        usage.put("completionTokens", 514);

        return new AiMetricsResponse(result.resultId(), quality, latency, usage);
    }
}
