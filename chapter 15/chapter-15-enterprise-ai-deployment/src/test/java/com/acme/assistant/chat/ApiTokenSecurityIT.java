package com.acme.assistant.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.health.DataSourceHealthContributorAutoConfiguration,org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration",
    "management.health.db.enabled=false",
    "assistant.security.bearer-token=test-token"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTokenSecurityIT {

    private static final String VALID_TOKEN = "Bearer test-token";
    private static final String INVALID_TOKEN = "Bearer wrong-token";

    private final HttpClient client = HttpClient.newHttpClient();

    @Value("${local.server.port}")
    private int port;

    @Test
    void shouldRejectAllApiEndpointsWithoutToken() throws Exception {
        assertUnauthorized(call("/api/chat", HttpMethod.POST,
            "{\"conversationId\":\"c1\",\"userId\":\"u1\",\"message\":\"hello\",\"context\":{}}", null));
        assertUnauthorized(call("/api/rag/query", HttpMethod.POST,
            "{\"query\":\"travel policy\"}", null));
        assertUnauthorized(call("/api/tools/execute", HttpMethod.POST,
            "{\"toolName\":\"calculator\",\"arguments\":{\"left\":5,\"right\":2,\"operator\":\"+\"}}", null));
        assertUnauthorized(call("/api/agents/run", HttpMethod.POST,
            "{\"instruction\":\"check finance budget\"}", null));
        assertUnauthorized(call("/api/health/ai", HttpMethod.GET, null, null));
        assertUnauthorized(call("/api/metrics/ai", HttpMethod.GET, null, null));
    }

    @Test
    void shouldRejectAllApiEndpointsWithInvalidToken() throws Exception {
        assertUnauthorized(call("/api/chat", HttpMethod.POST,
            "{\"conversationId\":\"c2\",\"userId\":\"u2\",\"message\":\"hello\",\"context\":{}}", INVALID_TOKEN));
        assertUnauthorized(call("/api/rag/query", HttpMethod.POST,
            "{\"query\":\"hr leave policy\"}", INVALID_TOKEN));
        assertUnauthorized(call("/api/tools/execute", HttpMethod.POST,
            "{\"toolName\":\"calculator\",\"arguments\":{\"left\":8,\"right\":3,\"operator\":\"-\"}}", INVALID_TOKEN));
        assertUnauthorized(call("/api/agents/run", HttpMethod.POST,
            "{\"instruction\":\"hr leave workflow\"}", INVALID_TOKEN));
        assertUnauthorized(call("/api/health/ai", HttpMethod.GET, null, INVALID_TOKEN));
        assertUnauthorized(call("/api/metrics/ai", HttpMethod.GET, null, INVALID_TOKEN));
    }

    @Test
    void shouldAllowAllApiEndpointsWithValidToken() throws Exception {
        HttpResponse<String> chat = call("/api/chat", HttpMethod.POST,
            "{\"conversationId\":\"c3\",\"userId\":\"u3\",\"message\":\"architecture fallback guidance\",\"context\":{}}", VALID_TOKEN);
        assertThat(chat.statusCode()).isEqualTo(200);
        assertThat(chat.body()).contains("\"conversationId\":\"c3\"");

        HttpResponse<String> rag = call("/api/rag/query", HttpMethod.POST,
            "{\"query\":\"travel reimbursement\"}", VALID_TOKEN);
        assertThat(rag.statusCode()).isEqualTo(200);
        assertThat(rag.body()).contains("answer");

        HttpResponse<String> tool = call("/api/tools/execute", HttpMethod.POST,
            "{\"toolName\":\"calculator\",\"arguments\":{\"left\":10,\"right\":2,\"operator\":\"/\"}}", VALID_TOKEN);
        assertThat(tool.statusCode()).isEqualTo(200);
        assertThat(tool.body()).contains("calculator");

        HttpResponse<String> agents = call("/api/agents/run", HttpMethod.POST,
            "{\"instruction\":\"finance budget planning\"}", VALID_TOKEN);
        assertThat(agents.statusCode()).isEqualTo(200);
        assertThat(agents.body()).contains("agent");

        HttpResponse<String> health = call("/api/health/ai", HttpMethod.GET, null, VALID_TOKEN);
        assertThat(health.statusCode()).isEqualTo(200);
        assertThat(health.body()).contains("assistant");

        HttpResponse<String> metrics = call("/api/metrics/ai", HttpMethod.GET, null, VALID_TOKEN);
        assertThat(metrics.statusCode()).isEqualTo(200);
        assertThat(metrics.body()).contains("ai.requests.total");
    }

    private HttpResponse<String> call(String path, HttpMethod method, String body, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path));

        if (token != null) {
            builder.header(HttpHeaders.AUTHORIZATION, token);
        }
        if (body != null) {
            builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }

        if (method == HttpMethod.GET) {
            builder.GET();
        } else {
            builder.POST(HttpRequest.BodyPublishers.ofString(body == null ? "" : body));
        }

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private void assertUnauthorized(HttpResponse<String> response) {
        assertThat(response.statusCode()).isEqualTo(401);
    }
}
