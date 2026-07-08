package com.acme.assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acme.assistant")
public record AcmeAssistantProperties(
        int defaultChunkSize,
        int defaultOverlap,
        int defaultTopK) {
}
