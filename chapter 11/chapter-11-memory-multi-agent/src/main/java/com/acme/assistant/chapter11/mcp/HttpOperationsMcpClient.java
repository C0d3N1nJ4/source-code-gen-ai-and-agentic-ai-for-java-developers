package com.acme.assistant.chapter11.mcp;

import com.acme.assistant.chapter11.config.McpEndpointsProperties;
import com.acme.assistant.chapter11.tools.EnterpriseToolService;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * HTTP-backed Operations MCP adapter with deterministic fallback.
 */
@Component
public class HttpOperationsMcpClient implements OperationsMcpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpOperationsMcpClient.class);

    private final WebClient webClient;
    private final McpEndpointsProperties properties;
    private final EnterpriseToolService enterpriseToolService;

    public HttpOperationsMcpClient(WebClient webClient, McpEndpointsProperties properties, EnterpriseToolService enterpriseToolService) {
        this.webClient = webClient;
        this.properties = properties;
        this.enterpriseToolService = enterpriseToolService;
    }

    @Override
    public Map<String, Object> restartServer(String environment, String serviceName) {
        try {
            return webClient.post()
                    .uri(properties.operationsBaseUrl() + "/mcp/ops/restart")
                    .bodyValue(Map.of("environment", environment, "serviceName", serviceName))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(2));
        } catch (RuntimeException ex) {
            log.info("Operations MCP unavailable, using local tool fallback");
            return enterpriseToolService.restartServer(environment, serviceName);
        }
    }

    @Override
    public Map<String, Object> health(String serviceName) {
        try {
            return webClient.get()
                    .uri(properties.operationsBaseUrl() + "/mcp/ops/health/{serviceName}", serviceName)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(2));
        } catch (RuntimeException ex) {
            return Map.of("service", serviceName, "status", "UNKNOWN");
        }
    }
}
