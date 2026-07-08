package com.acme.assistant.model;

import java.util.List;

public record AssistantChatResponse(
        String response,
        String employeeId,
        List<RemoteToolDescriptor> discoveredTools) {
}
