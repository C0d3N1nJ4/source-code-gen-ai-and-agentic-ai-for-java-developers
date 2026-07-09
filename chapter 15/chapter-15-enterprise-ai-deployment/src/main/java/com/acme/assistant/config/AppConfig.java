package com.acme.assistant.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public TaskExecutor assistantTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("assistant-");
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.initialize();
        return executor;
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
            .build();
    }

    @Bean
    public io.github.resilience4j.timelimiter.TimeLimiter aiTimeLimiter(AssistantProperties properties) {
        return io.github.resilience4j.timelimiter.TimeLimiter.of(Duration.ofMillis(properties.getResilience().getTimeoutMs()));
    }
}
