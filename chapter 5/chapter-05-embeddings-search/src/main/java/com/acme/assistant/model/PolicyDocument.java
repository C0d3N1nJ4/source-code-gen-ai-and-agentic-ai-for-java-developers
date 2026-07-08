package com.acme.assistant.model;

import java.util.Map;

public record PolicyDocument(
        String id,
        String title,
        String content,
        Map<String, String> metadata) {
}
