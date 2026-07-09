package com.example.chapter14.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acme.observability.cost")
public record ObservabilityCostProperties(
        double inputTokenUnitCost,
        double outputTokenUnitCost) {
}
