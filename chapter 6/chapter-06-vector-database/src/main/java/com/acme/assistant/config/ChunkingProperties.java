package com.acme.assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.chunking")
public record ChunkingProperties(int defaultChunkSize, int defaultOverlap) {
}
