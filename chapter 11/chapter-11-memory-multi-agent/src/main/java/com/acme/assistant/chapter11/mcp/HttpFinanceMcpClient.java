package com.acme.assistant.chapter11.mcp;

import com.acme.assistant.chapter11.config.McpEndpointsProperties;
import com.acme.assistant.chapter11.tools.EnterpriseToolService;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * HTTP-backed Finance MCP adapter with deterministic fallback.
 */
@Component
public class HttpFinanceMcpClient implements FinanceMcpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpFinanceMcpClient.class);

    private final WebClient webClient;
    private final McpEndpointsProperties properties;
    private final EnterpriseToolService enterpriseToolService;

    public HttpFinanceMcpClient(WebClient webClient, McpEndpointsProperties properties, EnterpriseToolService enterpriseToolService) {
        this.webClient = webClient;
        this.properties = properties;
        this.enterpriseToolService = enterpriseToolService;
    }

    @Override
    public Map<String, Object> estimateTravelExpenses(String destination, int days) {
        try {
            return webClient.post()
                    .uri(properties.financeBaseUrl() + "/mcp/finance/travel-estimate")
                    .bodyValue(Map.of("destination", destination, "days", days))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(2));
        } catch (RuntimeException ex) {
            log.info("Finance MCP unavailable, using local tool fallback");
            return enterpriseToolService.estimateTravelExpenses(destination, days);
        }
    }

    @Override
    public Map<String, Object> createExpenseClaim(String userId, String category, BigDecimal amount) {
        try {
            return webClient.post()
                    .uri(properties.financeBaseUrl() + "/mcp/finance/expense-claims")
                    .bodyValue(Map.of("userId", userId, "category", category, "amount", amount))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(2));
        } catch (RuntimeException ex) {
            return enterpriseToolService.createExpenseClaim(userId, category, amount);
        }
    }
}
