package com.acme.assistant.chapter12.tools;

import com.acme.assistant.chapter12.exceptions.BadRequestException;
import com.acme.assistant.chapter12.exceptions.ForbiddenException;
import com.acme.assistant.chapter12.security.AuthorizationService;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ToolSecurityService {

    private final AuthorizationService authorizationService;

    public ToolSecurityService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public String executeBusinessAction(String agent, String message) {
        String normalized = message.toLowerCase();
        if (normalized.contains("submit leave")) {
            requireRole("EMPLOYEE", "HR", "ADMIN");
            return "Leave request submitted.";
        }
        if (normalized.contains("approve leave")) {
            requireRole("HR", "ADMIN");
            return "Leave approved.";
        }
        if (normalized.contains("approve expense")) {
            requireRole("FINANCE", "ADMIN");
            return "Expense approved.";
        }
        if (normalized.contains("restart server")) {
            requireRole("OPERATIONS", "ADMIN");
            return "Server restart command scheduled.";
        }
        return "No privileged tool execution requested.";
    }

    public void validateToolInvocation(String toolName, Map<String, Object> parameters) {
        if (toolName == null || toolName.isBlank()) {
            throw new BadRequestException("Tool name is required");
        }
        if (parameters == null) {
            throw new BadRequestException("Tool parameters are required");
        }

        if ("submitExpense".equalsIgnoreCase(toolName)) {
            requireRole("EMPLOYEE", "FINANCE", "ADMIN");
            Object amountObject = parameters.get("amount");
            if (!(amountObject instanceof Number number) || number.doubleValue() <= 0) {
                throw new BadRequestException("submitExpense requires positive amount");
            }
        }
    }

    private void requireRole(String... roles) {
        for (String role : roles) {
            if (authorizationService.hasRole(role)) {
                return;
            }
        }
        throw new ForbiddenException("User is not authorized for requested tool operation");
    }
}
