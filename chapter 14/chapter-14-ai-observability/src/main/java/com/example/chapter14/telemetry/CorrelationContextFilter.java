package com.example.chapter14.telemetry;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationContextFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    public CorrelationContextFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = headerOrGenerated(request, "X-Request-ID");
        String conversationId = headerOrGenerated(request, "X-Conversation-ID");
        String workflowId = headerOrGenerated(request, "X-Workflow-ID");
        String userId = headerOrGenerated(request, "X-User-ID");

        Span span = tracer.nextSpan().name("http.request.correlation").start();
        try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
            String traceId = span.context().traceId();
            String spanId = span.context().spanId();

            MDC.put("traceId", traceId);
            MDC.put("spanId", spanId);
            MDC.put("requestId", requestId);
            MDC.put("conversationId", conversationId);
            MDC.put("workflowId", workflowId);
            MDC.put("userId", userId);

            response.setHeader("X-Trace-ID", traceId);
            response.setHeader("X-Span-ID", spanId);
            response.setHeader("X-Request-ID", requestId);
            response.setHeader("X-Conversation-ID", conversationId);
            response.setHeader("X-Workflow-ID", workflowId);

            filterChain.doFilter(request, response);
        } finally {
            span.end();
            MDC.clear();
        }
    }

    private String headerOrGenerated(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        return value == null || value.isBlank() ? headerName.toLowerCase() + "-" + UUID.randomUUID() : value;
    }
}
