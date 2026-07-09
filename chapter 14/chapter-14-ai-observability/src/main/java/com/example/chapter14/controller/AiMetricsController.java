package com.example.chapter14.controller;

import com.example.chapter14.dto.ObservabilityResponses.AgentMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.AiMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.BusinessMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.McpMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.RagMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.TokenMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.ToolMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.WorkflowMetricsApiResponse;
import com.example.chapter14.service.MetricsObservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class AiMetricsController {

    private final MetricsObservationService metricsObservationService;

    public AiMetricsController(MetricsObservationService metricsObservationService) {
        this.metricsObservationService = metricsObservationService;
    }

    @GetMapping("/ai")
    public AiMetricsApiResponse aiMetrics() {
        return metricsObservationService.aiMetrics();
    }

    @GetMapping("/rag")
    public RagMetricsApiResponse ragMetrics() {
        return metricsObservationService.ragMetrics();
    }

    @GetMapping("/tools")
    public ToolMetricsApiResponse toolMetrics() {
        return metricsObservationService.toolMetrics();
    }

    @GetMapping("/mcp")
    public McpMetricsApiResponse mcpMetrics() {
        return metricsObservationService.mcpMetrics();
    }

    @GetMapping("/agents")
    public AgentMetricsApiResponse agentMetrics() {
        return metricsObservationService.agentMetrics();
    }

    @GetMapping("/workflows")
    public WorkflowMetricsApiResponse workflowMetrics() {
        return metricsObservationService.workflowMetrics();
    }

    @GetMapping("/tokens")
    public TokenMetricsApiResponse tokenMetrics() {
        return metricsObservationService.tokenMetrics();
    }

    @GetMapping("/business")
    public BusinessMetricsApiResponse businessMetrics() {
        return metricsObservationService.businessMetrics();
    }
}
