package com.example.chapter14.service;

import com.example.chapter14.dto.ObservabilityResponses.AlertView;
import com.example.chapter14.dto.ObservabilityResponses.DashboardPanel;
import com.example.chapter14.dto.ObservabilityResponses.DashboardSummaryResponse;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardAlertService {

    private final MetricsObservationService metricsObservationService;
    private final AcmeObservabilityContextService contextService;

    public DashboardAlertService(
            MetricsObservationService metricsObservationService,
            AcmeObservabilityContextService contextService) {
        this.metricsObservationService = metricsObservationService;
        this.contextService = contextService;
    }

    public DashboardSummaryResponse summary() {
        var metrics = metricsObservationService.report();
        Map<String, Integer> thresholds = contextService.alertThresholds();

        List<DashboardPanel> panels = List.of(
                new DashboardPanel("Response Latency", metrics.modelMetrics().averageResponseLatencyMs() + " ms", "HEALTHY", "Average latency remains below the 5 second alert threshold."),
                new DashboardPanel("Token Usage", String.valueOf(metrics.tokenMetrics().totalTokens()), "HEALTHY", "Daily budget usage is at " + metrics.tokenMetrics().dailyBudgetUsedPercent() + "% ."),
                new DashboardPanel("MCP Availability", metrics.mcpMetrics().availabilityPercent() + "%", "HEALTHY", "MCP availability remains at or above operational target."),
                new DashboardPanel("Agent Workflow Duration", metrics.agentMetrics().workflowDurationMs() + " ms", "WATCH", "Finance workflows remain the longest-running specialist path."),
                new DashboardPanel("Business Outcomes", "19 incident workflows", "HEALTHY", "Leave, expense, and incident workflows continue to complete successfully."));

        List<AlertView> alerts = List.of(
                new AlertView(
                        "Response latency",
                        "Above " + thresholds.get("responseLatencyMs") + " ms",
                        "MEDIUM",
                        "Inspect finance-mcp latency and LLM inference spans first."),
                new AlertView(
                        "MCP availability",
                        "Below " + thresholds.get("mcpAvailabilityPercent") + "%",
                        "HIGH",
                        "Check finance-mcp and support-mcp health plus timeout spans."),
                new AlertView(
                        "Tool failure rate",
                        "Above " + thresholds.get("toolFailureRatePercent") + "%",
                        "HIGH",
                        "Review EstimateExpenses and incident-ticket execution logs."),
                new AlertView(
                        "Token budget",
                        "Daily usage above " + thresholds.get("dailyBudgetUsedPercent") + "%",
                        "MEDIUM",
                        "Compare prompt length, retrieval count, and model mix trends."));

        return new DashboardSummaryResponse(
                "Acme AI Operations Dashboard",
                panels,
                alerts,
                List.of(
                        "Expense workflows complete within target latency for most requests.",
                        "Knowledge retrieval remains strong for travel and release-note queries.",
                        "Incident escalation throughput remains stable."));
    }
}