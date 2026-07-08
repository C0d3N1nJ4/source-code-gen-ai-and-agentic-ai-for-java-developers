package com.acme.assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai")
public record AiProperties(
        String model,
        double temperature,
        Integer maxTokens,
        Integer ragTopK) {
}
