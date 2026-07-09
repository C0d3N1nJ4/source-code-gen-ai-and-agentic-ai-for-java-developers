package com.example.chapter14.controller;

import com.example.chapter14.dto.ObservabilityResponses.AlertView;
import com.example.chapter14.dto.ObservabilityResponses.CostMetrics;
import com.example.chapter14.dto.ObservabilityResponses.DashboardPanel;
import com.example.chapter14.dto.ObservabilityResponses.DashboardSummaryResponse;
import com.example.chapter14.dto.ObservabilityResponses.ImprovementOpportunity;
import com.example.chapter14.dto.ObservabilityResponses.ImprovementRecommendationsResponse;
import com.example.chapter14.dto.ObservabilityResponses.McpMetrics;
import com.example.chapter14.dto.ObservabilityResponses.MetricsReportResponse;
import com.example.chapter14.dto.ObservabilityResponses.ModelMetrics;
import com.example.chapter14.dto.ObservabilityResponses.ObservabilityArchitectureResponse;
import com.example.chapter14.dto.ObservabilityResponses.ObservabilityPillar;
import com.example.chapter14.dto.ObservabilityResponses.RagMetrics;
import com.example.chapter14.dto.ObservabilityResponses.StructuredLogEvent;
import com.example.chapter14.dto.ObservabilityResponses.StructuredLogResponse;
import com.example.chapter14.dto.ObservabilityResponses.TokenMetrics;
import com.example.chapter14.dto.ObservabilityResponses.ToolMetrics;
import com.example.chapter14.dto.ObservabilityResponses.TraceSimulationResponse;
import com.example.chapter14.dto.ObservabilityResponses.TraceSpanView;
import com.example.chapter14.dto.ObservabilityResponses.AgentMetrics;
import com.example.chapter14.service.DashboardAlertService;
import com.example.chapter14.service.MetricsObservationService;
import com.example.chapter14.service.ObservabilityArchitectureService;
import com.example.chapter14.service.ObservabilityImprovementService;
import com.example.chapter14.service.StructuredLoggingService;
import com.example.chapter14.service.TraceSimulationService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import java.util.List;
import java.util.Map;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiObservabilityController.class)
@DisplayName("AiObservabilityController Tests")
class AiObservabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TraceSimulationService traceSimulationService;

    @MockitoBean
    private MetricsObservationService metricsObservationService;

    @MockitoBean
    private StructuredLoggingService structuredLoggingService;

    @MockitoBean
    private DashboardAlertService dashboardAlertService;

    @MockitoBean
    private ObservabilityImprovementService observabilityImprovementService;

    @MockitoBean
    private ObservabilityArchitectureService observabilityArchitectureService;

        @MockitoBean
        private Tracer tracer;

        @BeforeEach
        void setUpTracer() {
                Span span = org.mockito.Mockito.mock(Span.class);
                TraceContext traceContext = org.mockito.Mockito.mock(TraceContext.class);
                Tracer.SpanInScope spanInScope = org.mockito.Mockito.mock(Tracer.SpanInScope.class);

                when(tracer.nextSpan()).thenReturn(span);
                when(span.name("http.request.correlation")).thenReturn(span);
                when(span.start()).thenReturn(span);
                when(span.context()).thenReturn(traceContext);
                when(traceContext.traceId()).thenReturn("trace-1401");
                when(traceContext.spanId()).thenReturn("span-1401");
                when(tracer.withSpan(span)).thenReturn(spanInScope);
        }

    @Test
    void shouldReturnHealth() throws Exception {
        mockMvc.perform(get("/api/ai-observability/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("chapter-14-ai-observability"));
    }

    @Test
    void shouldSimulateTrace() throws Exception {
        when(traceSimulationService.simulate(any()))
                .thenReturn(new TraceSimulationResponse(
                        "trace-1401",
                        "req-1401",
                        "conv-1401",
                        "wf-1401",
                        "EMP-1001",
                        "workflow",
                        List.of(new TraceSpanView("span-1", null, "assistant.request", "gateway", 35, "OK", Map.of("userId", "EMP-1001"))),
                        Map.of("gateway", 35L),
                        List.of("Finance Agent"),
                        List.of("finance-mcp")));

        mockMvc.perform(post("/api/ai-observability/traces/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                                                                                                .with(httpBasic("evaluator", "evaluator-pass"))
                        .content("""
                                {
                                  "conversationId":"conv-1401",
                                  "userId":"EMP-1001",
                                  "goal":"Estimate expenses",
                                  "includeFailurePath":true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traceId").value("trace-1401"));
    }

    @Test
    void shouldReturnMetrics() throws Exception {
        when(metricsObservationService.report())
                .thenReturn(new MetricsReportResponse(
                        "Acme Corporation Enterprise Assistant",
                        new ModelMetrics(Map.of("llama3.2", 100), 28, 742.0),
                        new TokenMetrics(420, 180, 600, 63),
                        new RagMetrics(3, 44.0, 0.93, 98),
                        new ToolMetrics(Map.of("EstimateExpenses", 82), 99, 72.0),
                        new McpMetrics(Map.of("finance-mcp", 510.0), 99, 1),
                        new AgentMetrics(Map.of("Finance Agent", 79), 611.0, 34),
                        new CostMetrics(48.75, 0.23, "token consumption"),
                        List.of("Stable")));

        mockMvc.perform(get("/api/ai-observability/metrics/report").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenMetrics.totalTokens").value(600));
    }

    @Test
    void shouldSimulateLogs() throws Exception {
        when(structuredLoggingService.simulate(any()))
                .thenReturn(new StructuredLogResponse(
                        "trace-1401",
                        List.of(new StructuredLogEvent("2026-07-02T12:00:01Z", "INFO", "prompt.constructed", Map.of("traceId", "trace-1401"))),
                        List.of("Mask PII"),
                        List.of("traceId", "spanId")));

        mockMvc.perform(post("/api/ai-observability/logs/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                                                                                                .with(httpBasic("evaluator", "evaluator-pass"))
                        .content("""
                                {
                                  "traceId":"trace-1401",
                                  "conversationId":"conv-1401",
                                  "promptVersion":"v4.3",
                                  "includeSensitiveMetadata":false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traceId").value("trace-1401"));
    }

    @Test
    void shouldReturnDashboardSummary() throws Exception {
        when(dashboardAlertService.summary())
                .thenReturn(new DashboardSummaryResponse(
                        "Acme AI Operations Dashboard",
                        List.of(new DashboardPanel("Response Latency", "742.0 ms", "HEALTHY", "Stable")),
                        List.of(new AlertView("Response latency", "Above 5000 ms", "MEDIUM", "Inspect finance-mcp")),
                        List.of("Expense workflows stable")));

        mockMvc.perform(get("/api/ai-observability/dashboards/summary").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dashboardName").value("Acme AI Operations Dashboard"));
    }

    @Test
    void shouldReturnRecommendations() throws Exception {
        when(observabilityImprovementService.recommendations())
                .thenReturn(new ImprovementRecommendationsResponse(
                        List.of(new ImprovementOpportunity("Prompt design", "Input tokens high", "Shorten prompt", "Lower cost")),
                        List.of("Token usage trends")));

        mockMvc.perform(get("/api/ai-observability/improvement/recommendations").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opportunities[0].area").value("Prompt design"));
    }

    @Test
    void shouldReturnArchitectureReport() throws Exception {
        when(observabilityArchitectureService.report())
                .thenReturn(new ObservabilityArchitectureResponse(
                        "Acme Corporation Enterprise Assistant",
                        List.of(new ObservabilityPillar("AI Evaluations", "Measure quality", List.of("workflow success"))),
                        List.of("chapter 13 evaluation suite"),
                        List.of("Operations Teams"),
                        List.of("Correlate telemetry")));

        mockMvc.perform(get("/api/ai-observability/architecture/report").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pillars[0].name").value("AI Evaluations"));
    }
}