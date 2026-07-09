package com.example.chapter13.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acme.ai.evaluations")
public record AiEvaluationStorageProperties(
        String resultJsonPath,
        String resultHtmlPath) {
}
