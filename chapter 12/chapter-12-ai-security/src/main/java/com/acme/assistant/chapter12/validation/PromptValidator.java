package com.acme.assistant.chapter12.validation;

import com.acme.assistant.chapter12.config.AiSecurityProperties;
import com.acme.assistant.chapter12.exceptions.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class PromptValidator {

    private final AiSecurityProperties properties;

    public PromptValidator(AiSecurityProperties properties) {
        this.properties = properties;
    }

    public void validate(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new BadRequestException("Prompt must not be blank");
        }
        if (prompt.length() > properties.getPrompt().getMaxPromptLength()) {
            throw new BadRequestException("Prompt exceeds maximum allowed length");
        }
    }
}
