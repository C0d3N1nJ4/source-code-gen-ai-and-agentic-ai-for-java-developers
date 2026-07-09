package com.example.chapter14.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acme.security")
public record ObservabilitySecurityProperties(
        String evaluatorUsername,
        String evaluatorPassword,
        String evaluatorRole) {
}
