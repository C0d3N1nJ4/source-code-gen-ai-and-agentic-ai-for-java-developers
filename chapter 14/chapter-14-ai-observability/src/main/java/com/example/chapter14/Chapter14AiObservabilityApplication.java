package com.example.chapter14;

import com.example.chapter14.config.ObservabilityCostProperties;
import com.example.chapter14.config.ObservabilitySecurityProperties;
import com.example.chapter14.config.ObservabilityThresholdProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    ObservabilitySecurityProperties.class,
    ObservabilityCostProperties.class,
    ObservabilityThresholdProperties.class
})
public class Chapter14AiObservabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(Chapter14AiObservabilityApplication.class, args);
    }
}