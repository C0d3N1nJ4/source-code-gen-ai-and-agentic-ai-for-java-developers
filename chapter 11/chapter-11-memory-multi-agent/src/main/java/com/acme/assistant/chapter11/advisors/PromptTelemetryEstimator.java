package com.acme.assistant.chapter11.advisors;

import org.springframework.stereotype.Component;

/**
 * Lightweight token estimator used for observability logs.
 */
@Component
public class PromptTelemetryEstimator {

    public int estimateTokens(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return Math.max(1, text.length() / 4);
    }
}
