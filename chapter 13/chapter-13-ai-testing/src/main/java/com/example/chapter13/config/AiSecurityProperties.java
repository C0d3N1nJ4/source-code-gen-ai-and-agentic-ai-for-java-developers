package com.example.chapter13.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acme.security")
public record AiSecurityProperties(
        String evaluatorUsername,
        String evaluatorPassword,
        String evaluatorRole) {
}
