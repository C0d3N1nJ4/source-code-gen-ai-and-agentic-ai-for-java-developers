package com.example.chapter14.model;

import java.util.Map;

public record TraceSpan(
        String spanId,
        String parentSpanId,
        String name,
        String component,
        long latencyMs,
        String status,
        Map<String, String> tags) {
}