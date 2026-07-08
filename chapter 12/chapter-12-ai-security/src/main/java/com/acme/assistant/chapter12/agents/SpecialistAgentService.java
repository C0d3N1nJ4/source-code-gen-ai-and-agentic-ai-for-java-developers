package com.acme.assistant.chapter12.agents;

import com.acme.assistant.chapter12.mcp.McpSecurityService;
import com.acme.assistant.chapter12.tools.ToolSecurityService;
import java.util.List;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class SpecialistAgentService {

    private final ChatModel chatModel;
    private final McpSecurityService mcpSecurityService;
    private final ToolSecurityService toolSecurityService;

    public SpecialistAgentService(
            ChatModel chatModel,
            McpSecurityService mcpSecurityService,
            ToolSecurityService toolSecurityService) {
        this.chatModel = chatModel;
        this.mcpSecurityService = mcpSecurityService;
        this.toolSecurityService = toolSecurityService;
    }

    public String execute(String username, String conversationId, String agent, String message, List<String> context, List<String> ragChunks) {
        String capability = switch (agent) {
            case "HR" -> "hr.lookupEmployee";
            case "FINANCE" -> "finance.submitExpense";
            case "OPERATIONS" -> "ops.restartServer";
            default -> "knowledge.search";
        };

        String mcpResult = mcpSecurityService.invokeCapability(username, conversationId, agent, capability, message);
        String toolResult = toolSecurityService.executeBusinessAction(agent, message);

        String prompt = "You are the " + agent + " specialist for Acme Corporation."
                + "\nConversation context: " + context
                + "\nRAG context: " + ragChunks
                + "\nMCP result: " + mcpResult
                + "\nTool result: " + toolResult
                + "\nUser request: " + message
                + "\nFollow authorization and business governance policies strictly.";

        try {
            return chatModel.call(prompt);
        } catch (RuntimeException ex) {
            return "Specialist " + agent + " completed with secure fallback response. " + toolResult;
        }
    }
}
