package com.acme.assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.semantic-search")
public record SemanticSearchProperties(int topK) {
}
