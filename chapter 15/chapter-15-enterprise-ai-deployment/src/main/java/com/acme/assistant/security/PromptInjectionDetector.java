package com.acme.assistant.security;

import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class PromptInjectionDetector {

    private static final List<String> SUSPICIOUS_PATTERNS = List.of(
        "ignore previous instructions",
        "reveal system prompt",
        "bypass security",
        "disable guardrails",
        "act as root"
    );

    public boolean isMalicious(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return false;
        }
        String normalized = prompt.toLowerCase(Locale.ROOT);
        return SUSPICIOUS_PATTERNS.stream().anyMatch(normalized::contains);
    }
}
