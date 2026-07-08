package com.acme.assistant.chapter12.dto;

public record ChatResponse(
        String conversationId,
        String response,
        String selectedAgent,
        double promptRiskScore,
        boolean requiresApproval,
        String approvalRequestId) {
}
