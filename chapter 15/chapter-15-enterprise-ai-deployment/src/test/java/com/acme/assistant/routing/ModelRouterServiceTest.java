package com.acme.assistant.routing;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.config.AssistantProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class ModelRouterServiceTest {

    @Test
    void shouldRouteCodingQuestionToCodingModel() {
        AssistantProperties properties = new AssistantProperties();
        ModelRouterService service = new ModelRouterService(properties, new MockEnvironment());

        ModelSelection result = service.selectModel("Can you write Java code for REST API");

        assertThat(result.model()).isEqualTo(properties.getRouting().getCodingModel());
        assertThat(result.route()).isEqualTo("coding");
    }
}
