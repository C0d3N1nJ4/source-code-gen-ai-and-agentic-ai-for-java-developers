package com.acme.assistant.chapter12.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class PromptRiskAnalyzer {

    public PromptRiskResult analyze(String prompt) {
        String text = prompt.toLowerCase(Locale.ROOT);
        List<String> findings = new ArrayList<>();
        int score = 0;

        if (text.contains("ignore previous instructions")) {
            findings.add("Prompt injection phrase detected: ignore previous instructions");
            score += 35;
        }
        if (text.contains("reveal system prompt") || text.contains("show system prompt")) {
            findings.add("System prompt exfiltration attempt detected");
            score += 25;
        }
        if (text.contains("act as administrator") || text.contains("you are admin")) {
            findings.add("Privilege escalation attempt detected");
            score += 20;
        }
        if (text.contains("bypass security") || text.contains("disable authorization")) {
            findings.add("Security bypass attempt detected");
            score += 20;
        }
        if (text.contains("execute hidden instructions")) {
            findings.add("Hidden instruction execution request detected");
            score += 20;
        }

        return new PromptRiskResult(Math.min(score, 100), findings);
    }

    public record PromptRiskResult(int score, List<String> findings) {
    }
}
