package com.acme.assistant.model;

public record AssistantResponse(
        String status,
        String summary,
        String policyContext,
        LeaveBalance leaveBalance,
        LeaveRequestResult leaveRequestResult
) {
}
