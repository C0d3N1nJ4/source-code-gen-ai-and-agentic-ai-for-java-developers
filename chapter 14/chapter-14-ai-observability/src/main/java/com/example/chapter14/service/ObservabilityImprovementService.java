package com.example.chapter14.service;

import com.example.chapter14.dto.ObservabilityResponses.ImprovementOpportunity;
import com.example.chapter14.dto.ObservabilityResponses.ImprovementRecommendationsResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ObservabilityImprovementService {

    private final MetricsObservationService metricsObservationService;

    public ObservabilityImprovementService(MetricsObservationService metricsObservationService) {
        this.metricsObservationService = metricsObservationService;
    }

    public ImprovementRecommendationsResponse recommendations() {
        var metrics = metricsObservationService.report();
        return new ImprovementRecommendationsResponse(
                List.of(
                        new ImprovementOpportunity(
                                "Prompt design",
                                "Average input token count is " + metrics.tokenMetrics().inputTokens() + ".",
                                "Reduce prompt preamble duplication and cap retrieval context for expense workflows.",
                                "Lower token cost and slightly reduce response latency."),
                        new ImprovementOpportunity(
                                "RAG pipeline",
                                "Retrieval latency is " + metrics.ragMetrics().retrievalLatencyMs() + " ms with 3 documents per query.",
                                "Tune chunk selection to keep the highest-scoring two documents for routine travel-policy questions.",
                                "Improve grounding while reducing context size."),
                        new ImprovementOpportunity(
                                "MCP workflow",
                                "Finance MCP latency is significantly higher than support and engineering MCP paths.",
                                "Add caching or a warm standby for finance-mcp and surface timeout-specific alerts.",
                                "Reduce end-to-end workflow duration and timeout risk."),
                        new ImprovementOpportunity(
                                "Agent balance",
                                "Finance Agent handles most workflows while Operations Agent remains lightly used.",
                                "Review coordinator routing heuristics and split incident-notification logic from expense estimation.",
                                "Reduce specialist imbalance and simplify debugging.")),
                List.of(
                        "Token usage trends",
                        "RAG similarity scores",
                        "MCP latency by server",
                        "Agent selection frequency",
                        "Workflow completion time"));
    }
}