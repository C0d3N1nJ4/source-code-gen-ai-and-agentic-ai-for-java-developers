package com.example.chapter14.config;

import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingFallbackConfiguration {

    @Bean
    @ConditionalOnMissingBean(Tracer.class)
    Tracer tracer() {
        return Tracer.NOOP;
    }
}