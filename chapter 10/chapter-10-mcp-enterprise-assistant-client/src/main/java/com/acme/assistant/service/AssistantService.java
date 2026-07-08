package com.acme.assistant.service;

import com.acme.assistant.model.AssistantChatRequest;
import com.acme.assistant.model.AssistantChatResponse;
import com.acme.assistant.model.RemoteToolDescriptor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AssistantService {

    private final SyncMcpToolCallbackProvider mcpToolCallbackProvider;
    private final LlmOrchestrator llmOrchestrator;

    public AssistantService(SyncMcpToolCallbackProvider mcpToolCallbackProvider, LlmOrchestrator llmOrchestrator) {
        this.mcpToolCallbackProvider = mcpToolCallbackProvider;
        this.llmOrchestrator = llmOrchestrator;
    }

    public AssistantChatResponse chat(AssistantChatRequest request) {
        String employeeId = request.employeeId() == null || request.employeeId().isBlank() ? "E1001" : request.employeeId();

        List<RemoteToolDescriptor> discoveredTools = Arrays.stream(mcpToolCallbackProvider.getToolCallbacks())
            .map(toolCallback -> new RemoteToolDescriptor(
                toolCallback.getToolDefinition().name(),
                toolCallback.getToolDefinition().description(),
                toolCallback.getToolDefinition().inputSchema()))
            .toList();
        String toolList = discoveredTools.stream().map(RemoteToolDescriptor::name).sorted().toList().toString();

        String systemMessage = """
                You are the Acme Enterprise Assistant.
                You are connected to a remote HR MCP server.
                Use remote tools when HR data or actions are required.
                Available remote tools: %s
                Respond with clear enterprise language and include key outcomes.
                """.formatted(toolList);

        String userMessage = "EmployeeId: %s\nRequest: %s".formatted(employeeId, request.message());
        String response = llmOrchestrator.generateResponse(systemMessage, userMessage);

        return new AssistantChatResponse(response, employeeId, discoveredTools);
    }
}
