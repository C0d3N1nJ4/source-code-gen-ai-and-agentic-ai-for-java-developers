package com.acme.assistant.chapter12.mcp;

import com.acme.assistant.chapter12.audit.AuditService;
import com.acme.assistant.chapter12.config.AiSecurityProperties;
import com.acme.assistant.chapter12.exceptions.ForbiddenException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class McpSecurityService {

    private final AiSecurityProperties properties;
    private final AuditService auditService;

    private final Map<String, Set<String>> capabilitiesByAgent = Map.of(
            "HR", Set.of("hr.lookupEmployee", "hr.submitLeave", "hr.approveLeave"),
            "FINANCE", Set.of("finance.submitExpense", "finance.approveExpense"),
            "OPERATIONS", Set.of("ops.restartServer"),
            "KNOWLEDGE", Set.of("knowledge.search"));

    public McpSecurityService(AiSecurityProperties properties, AuditService auditService) {
        this.properties = properties;
        this.auditService = auditService;
    }

    public List<String> allowedCapabilities(String agent) {
        return capabilitiesByAgent.getOrDefault(agent.toUpperCase(), Set.of()).stream().sorted().toList();
    }

    public String invokeCapability(String username, String conversationId, String agent, String capability, String payload) {
        if (!capabilitiesByAgent.getOrDefault(agent.toUpperCase(), Set.of()).contains(capability)) {
            throw new ForbiddenException("Capability " + capability + " is not allowed for agent " + agent);
        }

        int retries = properties.getMcp().getRetries();
        int timeout = properties.getMcp().getTimeoutMillis();
        RuntimeException lastException = null;

        for (int attempt = 0; attempt <= retries; attempt++) {
            try {
                String response = CompletableFuture.supplyAsync(() -> "MCP capability " + capability + " executed with payload " + payload)
                        .orTimeout(timeout, TimeUnit.MILLISECONDS)
                        .join();
                auditService.record(username, conversationId, agent, "MCP_INVOKE", "ALLOW", 0, "capability=" + capability);
                return response;
            } catch (RuntimeException ex) {
                lastException = ex;
            }
        }

        auditService.record(username, conversationId, agent, "MCP_INVOKE", "DENY", 0, "capability=" + capability + ", timeout=" + Duration.ofMillis(timeout));
        throw new ForbiddenException("MCP invocation failed after retry policy: " + lastException.getMessage());
    }
}
