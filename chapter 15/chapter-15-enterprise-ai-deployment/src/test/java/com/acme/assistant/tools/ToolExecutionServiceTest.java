package com.acme.assistant.tools;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.config.AssistantProperties;
import com.acme.assistant.observability.AiMetricsService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class ToolExecutionServiceTest {

    @Test
    void shouldExecuteCalculatorTool() {
        AssistantProperties properties = new AssistantProperties();
        properties.getTools().setAllowList(java.util.List.of("calculator"));

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.initialize();

        ToolExecutionService service = new ToolExecutionService(
            properties,
            executor,
            new AiMetricsService(new SimpleMeterRegistry())
        );

        String result = service.execute("calculator", Map.of("left", 10, "right", 2, "operator", "/"));

        assertThat(result).isEqualTo("5.0");
    }
}
