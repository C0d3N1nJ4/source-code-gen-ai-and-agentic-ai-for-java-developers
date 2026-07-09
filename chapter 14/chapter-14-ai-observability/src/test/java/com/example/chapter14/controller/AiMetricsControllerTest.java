package com.example.chapter14.controller;

import com.example.chapter14.dto.ObservabilityResponses.AiMetricsApiResponse;
import com.example.chapter14.dto.ObservabilityResponses.CostMetrics;
import com.example.chapter14.dto.ObservabilityResponses.ModelMetrics;
import com.example.chapter14.dto.ObservabilityResponses.TokenMetrics;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import com.example.chapter14.service.MetricsObservationService;
import io.micrometer.tracing.Tracer;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiMetricsController.class)
class AiMetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MetricsObservationService metricsObservationService;

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
    void shouldReturnCorrelationHeaders() throws Exception {
        mockMvc.perform(get("/metrics/ai"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Trace-ID"))
                .andExpect(header().exists("X-Request-ID"));
    }

    @Test
    void shouldReturnAiMetricsForEvaluator() throws Exception {
        when(metricsObservationService.aiMetrics()).thenReturn(new AiMetricsApiResponse(
                "Acme Corporation Enterprise Assistant",
                new ModelMetrics(Map.of("llama3.1", 100), 28, 742.0),
                new TokenMetrics(420, 180, 600, 63),
                new CostMetrics(48.0, 0.24, "token consumption"),
                742.0));

        mockMvc.perform(get("/metrics/ai").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.platformName").value("Acme Corporation Enterprise Assistant"));
    }
}
