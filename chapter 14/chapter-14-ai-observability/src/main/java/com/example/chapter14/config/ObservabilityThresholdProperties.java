package com.example.chapter14.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acme.observability.thresholds")
public record ObservabilityThresholdProperties(
        int responseLatencyMs,
        int toolFailureRatePercent,
        int mcpTimeoutRatePercent,
        double groundingScoreMin,
        int dailyTokenBudget) {
}
