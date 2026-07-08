package com.acme.assistant.moderation;

import com.acme.assistant.model.PromptSafetyAssessment;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class PromptInjectionHeuristicService {

    private static final List<String> HIGH_RISK_MARKERS = List.of(
            "ignore previous instructions",
            "reveal your system prompt",
            "print the hidden system message",
            "bypass security",
            "act as root",
            "disregard policy",
            "override your instructions",
            "developer mode enabled",
            "exfiltrate",
            "leak secrets");

    public PromptSafetyAssessment assess(String inputText) {
        if (inputText == null || inputText.isBlank()) {
            return new PromptSafetyAssessment(
                    false,
                    "Input is blank and cannot be evaluated safely",
                    "INVALID_INPUT",
                    "Provide a non-empty input before processing");
        }

        String normalized = inputText.toLowerCase(Locale.ROOT);
        for (String marker : HIGH_RISK_MARKERS) {
            if (normalized.contains(marker)) {
                return new PromptSafetyAssessment(
                        false,
                        "Detected suspicious instruction pattern: " + marker,
                        "PROMPT_INJECTION",
                        "Reject request and route to human review");
            }
        }

        return new PromptSafetyAssessment(
                true,
                "No known prompt-injection markers detected",
                "NONE",
                "Proceed with normal processing and monitor logs");
    }

    public void rejectIfUnsafe(String inputText) {
        PromptSafetyAssessment assessment = assess(inputText);
        if (!assessment.safe()) {
            throw new IllegalArgumentException("Unsafe input rejected: " + assessment.reason());
        }
    }
}
