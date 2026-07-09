package com.acme.assistant.security;

import org.springframework.stereotype.Service;

@Service
public class GuardrailService {

    private final PromptInjectionDetector detector;
    private final PiiRedactor piiRedactor;

    public GuardrailService(PromptInjectionDetector detector, PiiRedactor piiRedactor) {
        this.detector = detector;
        this.piiRedactor = piiRedactor;
    }

    public String sanitizePrompt(String prompt) {
        return piiRedactor.redact(prompt);
    }

    public boolean shouldBlock(String prompt) {
        return detector.isMalicious(prompt);
    }
}
