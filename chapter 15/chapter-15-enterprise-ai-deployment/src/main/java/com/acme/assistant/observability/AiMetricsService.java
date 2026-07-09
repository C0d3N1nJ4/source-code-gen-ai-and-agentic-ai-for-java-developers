package com.acme.assistant.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class AiMetricsService {

    private final MeterRegistry meterRegistry;
    private final Counter aiRequests;
    private final Counter aiToolCalls;
    private final Counter aiAgentExecutions;
    private final Counter aiMcpCalls;
    private final Counter aiFallbacks;

    public AiMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.aiRequests = meterRegistry.counter("ai.requests.total");
        this.aiToolCalls = meterRegistry.counter("ai.tool.calls.total");
        this.aiAgentExecutions = meterRegistry.counter("ai.agent.executions");
        this.aiMcpCalls = meterRegistry.counter("ai.mcp.calls");
        this.aiFallbacks = meterRegistry.counter("ai.fallbacks");
    }

    public void recordRequest() {
        aiRequests.increment();
    }

    public void recordModelSelected(String model) {
        meterRegistry.counter("ai.model.selected", "model", model).increment();
    }

    public void recordModelLatency(String model, long ms) {
        Timer.builder("ai.model.latency")
            .tag("model", model)
            .register(meterRegistry)
            .record(ms, TimeUnit.MILLISECONDS);
    }

    public void recordToolCall() {
        aiToolCalls.increment();
    }

    public void recordRagDocuments(int count) {
        meterRegistry.summary("ai.rag.documents").record(count);
    }

    public void recordEstimatedTokens(int tokens) {
        meterRegistry.summary("ai.tokens.estimated").record(tokens);
    }

    public void recordAgentExecution() {
        aiAgentExecutions.increment();
    }

    public void recordMcpCall() {
        aiMcpCalls.increment();
    }

    public void recordFallback() {
        aiFallbacks.increment();
    }
}
