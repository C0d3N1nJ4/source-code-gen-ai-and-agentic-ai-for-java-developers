package com.acme.assistant.template;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class PromptTemplateServiceTest {

    private final PromptTemplateService promptTemplateService = new PromptTemplateService();

    @Test
    void render_shouldInjectVariablesFromTemplate() {
        String rendered = promptTemplateService.render("explain-java-concept.st", Map.of(
                "topic", "Spring Boot auto-configuration",
                "audience", "enterprise Java developers",
                "format", "bullet points"));

        assertTrue(rendered.contains("Spring Boot auto-configuration"));
        assertTrue(rendered.contains("enterprise Java developers"));
        assertTrue(rendered.contains("bullet points"));
    }
}
