package com.acme.assistant.chapter11;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point for Chapter 11 Acme Enterprise Assistant reference implementation.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Chapter11MemoryMultiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(Chapter11MemoryMultiAgentApplication.class, args);
    }
}
