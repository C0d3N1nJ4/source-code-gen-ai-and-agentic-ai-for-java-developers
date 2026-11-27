package com.acme.assistant.ask;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnabledIfSystemProperty(named = "acme.live", matches = "true")
class LiveModelTest {
    @Autowired AssistantService service;

    @Test void configuredProviderReturnsNonemptyAnswer() {
        assertFalse(service.ask("In one short sentence, what is Java?").isBlank());
    }
}
