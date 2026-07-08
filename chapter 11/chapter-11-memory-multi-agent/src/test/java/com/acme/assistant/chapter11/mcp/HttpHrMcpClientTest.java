package com.acme.assistant.chapter11.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.chapter11.config.McpEndpointsProperties;
import com.acme.assistant.chapter11.tools.EnterpriseToolService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class HttpHrMcpClientTest {

    @Test
    void shouldFallbackToLocalToolWhenRemoteUnavailable() {
        HttpHrMcpClient client = new HttpHrMcpClient(
                WebClient.builder().build(),
                new McpEndpointsProperties("http://localhost:1", "http://localhost:2", "http://localhost:3"),
                new EnterpriseToolService());

        Map<String, Object> result = client.submitLeave("EMP-1", "Friday", "Testing");

        assertThat(result).containsEntry("status", "SUBMITTED");
    }
}
