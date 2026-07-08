package com.acme.hrmcp.integration;

import io.modelcontextprotocol.client.McpSyncClient;
import org.junit.jupiter.api.Test;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=18091",
                "spring.datasource.url=jdbc:h2:mem:hrmcp;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.ai.mcp.client.enabled=true",
                "spring.ai.mcp.client.type=sync",
                "spring.ai.mcp.client.initialized=false",
                "spring.ai.mcp.client.streamable-http.connections.local-hr.url=http://localhost:18091",
                "spring.ai.mcp.client.streamable-http.connections.local-hr.endpoint=/mcp"
        })
class McpStreamableHttpIntegrationTest {

    @Autowired
    private List<McpSyncClient> mcpSyncClients;

    @Autowired
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    @Test
    void discoversAndInvokesToolsOverMcpStreamableHttpTransport() {
        for (McpSyncClient mcpSyncClient : mcpSyncClients) {
            mcpSyncClient.initialize();
        }

        ToolCallback[] callbacks = toolCallbackProvider.getToolCallbacks();

        assertTrue(callbacks.length >= 4, "Expected at least 4 remote MCP tools");
        assertTrue(hasToolEndingWith(callbacks, "findEmployee"), "Expected findEmployee tool");
        assertTrue(hasToolEndingWith(callbacks, "getLeaveBalance"), "Expected getLeaveBalance tool");

        ToolCallback findEmployeeCallback = findToolEndingWith(callbacks, "findEmployee");
        String result = findEmployeeCallback.call("{\"employeeId\":\"E1001\"}");

        assertNotNull(result);
        assertTrue(result.contains("E1001"), "Expected employee id in tool result payload");
    }

    private boolean hasToolEndingWith(ToolCallback[] callbacks, String suffix) {
        for (ToolCallback callback : callbacks) {
            if (callback.getToolDefinition().name().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private ToolCallback findToolEndingWith(ToolCallback[] callbacks, String suffix) {
        for (ToolCallback callback : callbacks) {
            if (callback.getToolDefinition().name().endsWith(suffix)) {
                return callback;
            }
        }
        throw new IllegalStateException("Could not find tool callback ending with " + suffix);
    }
}
