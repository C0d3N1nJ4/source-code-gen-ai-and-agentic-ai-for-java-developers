package com.acme.assistant.service;

import com.acme.assistant.model.AssistantChatRequest;
import com.acme.assistant.model.AssistantChatResponse;
import com.acme.assistant.model.RemoteToolDescriptor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AssistantServiceTest {

    @Test
    void chatBuildsResponseFromDiscoveredToolsAndLlm() {
    SyncMcpToolCallbackProvider mcpToolCallbackProvider = mock(SyncMcpToolCallbackProvider.class);
        LlmOrchestrator llmOrchestrator = mock(LlmOrchestrator.class);

    ToolDefinition leaveBalanceDefinition = mock(ToolDefinition.class);
    when(leaveBalanceDefinition.name()).thenReturn("getLeaveBalance");
    when(leaveBalanceDefinition.description()).thenReturn("desc");
    when(leaveBalanceDefinition.inputSchema()).thenReturn("{\"type\":\"object\"}");

    ToolDefinition submitLeaveDefinition = mock(ToolDefinition.class);
    when(submitLeaveDefinition.name()).thenReturn("submitLeaveRequest");
    when(submitLeaveDefinition.description()).thenReturn("desc");
    when(submitLeaveDefinition.inputSchema()).thenReturn("{\"type\":\"object\"}");

    ToolCallback leaveBalanceToolCallback = mock(ToolCallback.class);
    when(leaveBalanceToolCallback.getToolDefinition()).thenReturn(leaveBalanceDefinition);

    ToolCallback submitLeaveToolCallback = mock(ToolCallback.class);
    when(submitLeaveToolCallback.getToolDefinition()).thenReturn(submitLeaveDefinition);

    when(mcpToolCallbackProvider.getToolCallbacks())
        .thenReturn(new ToolCallback[] {leaveBalanceToolCallback, submitLeaveToolCallback});

        when(llmOrchestrator.generateResponse(contains("Available remote tools"), eq("EmployeeId: E1001\nRequest: Need leave help")))
                .thenReturn("You have enough balance and I submitted your request.");

    AssistantService service = new AssistantService(mcpToolCallbackProvider, llmOrchestrator);
        AssistantChatResponse response = service.chat(new AssistantChatRequest("Need leave help", null));

        assertEquals("E1001", response.employeeId());
        assertEquals(2, response.discoveredTools().size());
    assertEquals("getLeaveBalance", response.discoveredTools().getFirst().name());
    assertEquals("{\"type\":\"object\"}", response.discoveredTools().getFirst().inputSchema());
        assertEquals("You have enough balance and I submitted your request.", response.response());
    }
}
