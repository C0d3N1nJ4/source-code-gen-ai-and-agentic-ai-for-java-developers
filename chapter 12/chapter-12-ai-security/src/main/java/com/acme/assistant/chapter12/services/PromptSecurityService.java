package com.acme.assistant.chapter12.services;

import com.acme.assistant.chapter12.config.AiSecurityProperties;
import com.acme.assistant.chapter12.validation.PromptRiskAnalyzer;
import com.acme.assistant.chapter12.validation.PromptValidator;
import org.springframework.stereotype.Service;

@Service
public class PromptSecurityService {

    private final PromptValidator promptValidator;
    private final PromptRiskAnalyzer promptRiskAnalyzer;
    private final AiSecurityProperties properties;

    public PromptSecurityService(
            PromptValidator promptValidator,
            PromptRiskAnalyzer promptRiskAnalyzer,
            AiSecurityProperties properties) {
        this.promptValidator = promptValidator;
        this.promptRiskAnalyzer = promptRiskAnalyzer;
        this.properties = properties;
    }

    public PromptSecurityResult validatePrompt(String prompt) {
        promptValidator.validate(prompt);
        PromptRiskAnalyzer.PromptRiskResult result = promptRiskAnalyzer.analyze(prompt);
        boolean blocked = result.score() >= properties.getPrompt().getBlockThreshold();
        return new PromptSecurityResult(result.score(), blocked, result.findings());
    }

    public record PromptSecurityResult(int riskScore, boolean blocked, java.util.List<String> findings) {
    }
}
