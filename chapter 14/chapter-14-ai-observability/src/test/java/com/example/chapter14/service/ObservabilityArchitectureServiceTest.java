package com.example.chapter14.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservabilityArchitectureServiceTest {

    private final ObservabilityArchitectureService service = new ObservabilityArchitectureService();

    @Test
    void shouldExposeFourObservabilityPillarsIncludingAiEvaluations() {
        var report = service.report();

        assertThat(report.pillars()).hasSize(4);
        assertThat(report.pillars()).anyMatch(pillar -> "AI Evaluations".equals(pillar.name()));
        assertThat(report.telemetrySources()).contains("chapter 13 evaluation suite");
    }
}