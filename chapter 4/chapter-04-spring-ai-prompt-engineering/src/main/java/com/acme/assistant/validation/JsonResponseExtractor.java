package com.acme.assistant.validation;

import org.springframework.stereotype.Component;

@Component
public class JsonResponseExtractor {

    public String extract(String responseText) {
        if (responseText == null || responseText.isBlank()) {
            throw new IllegalArgumentException("AI response cannot be blank");
        }

        String cleaned = responseText.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        return cleaned.trim();
    }
}
