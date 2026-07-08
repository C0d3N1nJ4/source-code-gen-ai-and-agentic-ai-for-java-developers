package com.acme.assistant.chapter11.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Creates dedicated executor for parallel specialist-agent execution.
 */
@Configuration
public class ExecutorConfig {

    @Bean(name = "agentExecutionExecutor")
    public Executor agentExecutionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("agent-exec-");
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(64);
        executor.initialize();
        return executor;
    }
}
