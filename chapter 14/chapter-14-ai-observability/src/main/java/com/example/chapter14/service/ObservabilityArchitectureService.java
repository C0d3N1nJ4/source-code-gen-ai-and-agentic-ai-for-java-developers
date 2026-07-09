package com.example.chapter14.service;

import com.example.chapter14.dto.ObservabilityResponses.ObservabilityArchitectureResponse;
import com.example.chapter14.dto.ObservabilityResponses.ObservabilityPillar;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ObservabilityArchitectureService {

    public ObservabilityArchitectureResponse report() {
        return new ObservabilityArchitectureResponse(
                "Acme Corporation Enterprise Assistant",
                List.of(
                        new ObservabilityPillar("Metrics", "Measure platform health and trends", List.of("token usage", "latency", "MCP availability", "agent duration")),
                        new ObservabilityPillar("Logs", "Capture detailed operational events", List.of("prompt metadata", "tool execution", "MCP fallback", "security events")),
                        new ObservabilityPillar("Traces", "Visualize complete AI workflows", List.of("prompt spans", "RAG spans", "agent spans", "enterprise service spans")),
                        new ObservabilityPillar("AI Evaluations", "Measure quality and business outcomes", List.of("prompt quality", "RAG groundedness", "workflow success", "memory correctness"))),
                List.of(
                        "API gateway",
                        "Spring AI application with chapter 13 assistant baseline",
                        "memory subsystem",
                        "RAG retrieval",
                        "specialist agents",
                        "MCP servers",
                        "enterprise services",
                        "chapter 13 assistant baseline",
                        "chapter 13 evaluation suite"),
                List.of(
                        "Software Engineers",
                        "AI Engineers",
                        "Platform Engineers",
                        "Security Teams",
                        "Operations Teams",
                        "Business Owners"),
                List.of(
                        "Instrument the entire AI workflow rather than only the final response.",
                        "Correlate metrics, logs, traces, and evaluations with shared identifiers.",
                        "Design telemetry as a built-in platform capability, not an afterthought."));
    }
}