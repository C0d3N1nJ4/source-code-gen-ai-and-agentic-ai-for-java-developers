package com.acme.assistant.mcp;

import com.acme.assistant.config.AssistantProperties;
import com.acme.assistant.observability.AiMetricsService;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class McpClientService {

    private final AssistantProperties properties;
    private final WebClient webClient;
    private final AiMetricsService metricsService;

    public McpClientService(AssistantProperties properties, WebClient webClient, AiMetricsService metricsService) {
        this.properties = properties;
        this.webClient = webClient;
        this.metricsService = metricsService;
    }

    public String invoke(String operation, Map<String, Object> payload) {
        if (!properties.getMcp().getAllowList().contains(operation)) {
            return "MCP operation denied by allow list";
        }
        try {
            String response = webClient.post()
                .uri(properties.getMcp().getBaseUrl() + "/mcp/" + operation)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            metricsService.recordMcpCall();
            return response == null ? "MCP empty response" : response;
        } catch (Exception ex) {
            return "MCP fallback response: " + ex.getMessage();
        }
    }
}
