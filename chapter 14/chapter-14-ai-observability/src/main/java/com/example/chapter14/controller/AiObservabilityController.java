package com.example.chapter14.controller;

import com.example.chapter14.dto.ObservabilityRequests.StructuredLogRequest;
import com.example.chapter14.dto.ObservabilityRequests.TraceSimulationRequest;
import com.example.chapter14.dto.ObservabilityResponses.DashboardSummaryResponse;
import com.example.chapter14.dto.ObservabilityResponses.ImprovementRecommendationsResponse;
import com.example.chapter14.dto.ObservabilityResponses.MetricsReportResponse;
import com.example.chapter14.dto.ObservabilityResponses.ObservabilityArchitectureResponse;
import com.example.chapter14.dto.ObservabilityResponses.StructuredLogResponse;
import com.example.chapter14.dto.ObservabilityResponses.TraceSimulationResponse;
import com.example.chapter14.service.DashboardAlertService;
import com.example.chapter14.service.MetricsObservationService;
import com.example.chapter14.service.ObservabilityArchitectureService;
import com.example.chapter14.service.ObservabilityImprovementService;
import com.example.chapter14.service.StructuredLoggingService;
import com.example.chapter14.service.TraceSimulationService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai-observability")
public class AiObservabilityController {

    private final TraceSimulationService traceSimulationService;
    private final MetricsObservationService metricsObservationService;
    private final StructuredLoggingService structuredLoggingService;
    private final DashboardAlertService dashboardAlertService;
    private final ObservabilityImprovementService observabilityImprovementService;
    private final ObservabilityArchitectureService observabilityArchitectureService;

    public AiObservabilityController(
            TraceSimulationService traceSimulationService,
            MetricsObservationService metricsObservationService,
            StructuredLoggingService structuredLoggingService,
            DashboardAlertService dashboardAlertService,
            ObservabilityImprovementService observabilityImprovementService,
            ObservabilityArchitectureService observabilityArchitectureService) {
        this.traceSimulationService = traceSimulationService;
        this.metricsObservationService = metricsObservationService;
        this.structuredLoggingService = structuredLoggingService;
        this.dashboardAlertService = dashboardAlertService;
        this.observabilityImprovementService = observabilityImprovementService;
        this.observabilityArchitectureService = observabilityArchitectureService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "chapter-14-ai-observability");
    }

    @PostMapping("/traces/simulate")
    public TraceSimulationResponse simulateTrace(@Valid @RequestBody TraceSimulationRequest request) {
        return traceSimulationService.simulate(request);
    }

    @GetMapping("/metrics/report")
    public MetricsReportResponse metricsReport() {
        return metricsObservationService.report();
    }

    @PostMapping("/logs/simulate")
    public StructuredLogResponse simulateLogs(@Valid @RequestBody StructuredLogRequest request) {
        return structuredLoggingService.simulate(request);
    }

    @GetMapping("/dashboards/summary")
    public DashboardSummaryResponse dashboardSummary() {
        return dashboardAlertService.summary();
    }

    @GetMapping("/improvement/recommendations")
    public ImprovementRecommendationsResponse recommendations() {
        return observabilityImprovementService.recommendations();
    }

    @GetMapping("/architecture/report")
    public ObservabilityArchitectureResponse architectureReport() {
        return observabilityArchitectureService.report();
    }
}