package com.acme.assistant.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.config.AssistantProperties;
import com.acme.assistant.observability.AiMetricsService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class McpClientServiceTest {

    @Test
    void shouldCallAllowedMcpOperation() {
        WireMockServer server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        server.start();
        server.stubFor(post(urlEqualTo("/mcp/hr.lookup")).willReturn(aResponse().withStatus(200).withBody("{\"status\":\"ok\"}")));

        try {
            AssistantProperties properties = new AssistantProperties();
            properties.getMcp().setBaseUrl(server.baseUrl());
            properties.getMcp().setAllowList(java.util.List.of("hr.lookup"));

            McpClientService service = new McpClientService(
                properties,
                WebClient.builder().build(),
                new AiMetricsService(new SimpleMeterRegistry())
            );

            String result = service.invoke("hr.lookup", Map.of("employeeId", "E-123"));
            assertThat(result).contains("ok");
        } finally {
            server.stop();
        }
    }
}
