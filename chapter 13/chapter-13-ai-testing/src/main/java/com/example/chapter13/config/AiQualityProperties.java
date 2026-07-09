package com.example.chapter13.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acme.ai.quality")
public record AiQualityProperties(
        int promptAccuracyThreshold,
        int groundedResponsesThreshold,
        int toolSelectionThreshold,
        int workflowCompletionThreshold,
        int unauthorizedToolCallsThreshold,
        int hallucinationRateMaxThreshold) {
}
