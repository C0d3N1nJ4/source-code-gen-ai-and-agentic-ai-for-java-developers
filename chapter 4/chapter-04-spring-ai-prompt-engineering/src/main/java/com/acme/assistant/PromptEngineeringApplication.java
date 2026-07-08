package com.acme.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PromptEngineeringApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromptEngineeringApplication.class, args);
    }
}
