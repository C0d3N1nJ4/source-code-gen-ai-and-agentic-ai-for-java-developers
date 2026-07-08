package com.acme.assistant.chapter12.dto;

import java.util.List;

public record MemoryResponse(String conversationId, List<MemoryMessageResponse> messages) {
}
