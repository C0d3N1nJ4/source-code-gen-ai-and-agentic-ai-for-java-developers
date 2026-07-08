package com.acme.assistant.chapter11.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized MCP endpoint configuration.
 */
@ConfigurationProperties(prefix = "acme.assistant.mcp")
public record McpEndpointsProperties(
        String hrBaseUrl,
        String financeBaseUrl,
        String operationsBaseUrl
) {
}
