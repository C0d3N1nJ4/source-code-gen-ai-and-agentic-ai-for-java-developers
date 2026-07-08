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
 * HTTP-backed HR MCP adapter with deterministic fallback to local enterprise tool.
 */
@Component
public class HttpHrMcpClient implements HrMcpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpHrMcpClient.class);

    private final WebClient webClient;
    private final McpEndpointsProperties properties;
    private final EnterpriseToolService enterpriseToolService;

    public HttpHrMcpClient(WebClient webClient, McpEndpointsProperties properties, EnterpriseToolService enterpriseToolService) {
        this.webClient = webClient;
        this.properties = properties;
        this.enterpriseToolService = enterpriseToolService;
    }

    @Override
    public Map<String, Object> submitLeave(String userId, String leaveDate, String reason) {
        try {
            return webClient.post()
                    .uri(properties.hrBaseUrl() + "/mcp/hr/leave")
                    .bodyValue(Map.of("userId", userId, "leaveDate", leaveDate, "reason", reason))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(2));
        } catch (RuntimeException ex) {
            log.info("HR MCP unavailable, using local tool fallback");
            return enterpriseToolService.submitLeave(userId, leaveDate, reason);
        }
    }

    @Override
    public Map<String, Object> lookupEmployee(String employeeId) {
        try {
            return webClient.get()
                    .uri(properties.hrBaseUrl() + "/mcp/hr/employees/{id}", employeeId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(2));
        } catch (RuntimeException ex) {
            return enterpriseToolService.lookupEmployee(employeeId);
        }
    }
}
