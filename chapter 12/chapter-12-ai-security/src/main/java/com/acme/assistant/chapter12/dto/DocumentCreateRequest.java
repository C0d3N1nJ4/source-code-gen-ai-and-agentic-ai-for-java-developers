package com.acme.assistant.chapter12.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record DocumentCreateRequest(
        @NotBlank(message = "title is required") String title,
        @NotBlank(message = "classification is required") String classification,
        String ownerUserId,
        @NotBlank(message = "content is required") String content,
        @NotNull(message = "metadata is required") Map<String, Object> metadata) {
}
