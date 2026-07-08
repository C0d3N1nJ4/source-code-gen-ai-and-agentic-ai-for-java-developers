package com.acme.assistant.chapter11.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EnterpriseToolServiceTest {

    private final EnterpriseToolService service = new EnterpriseToolService();

    @Test
    void shouldEstimateTravelExpenses() {
        Map<String, Object> result = service.estimateTravelExpenses("Amsterdam", 4);

        assertThat(result).containsEntry("destination", "Amsterdam");
        assertThat(result.get("estimatedTotal").toString()).isEqualTo("1680.00");
    }

    @Test
    void shouldCreateExpenseClaim() {
        Map<String, Object> result = service.createExpenseClaim("EMP-1", "travel", new BigDecimal("88.80"));

        assertThat(result).containsEntry("status", "CREATED");
        assertThat(result).containsEntry("category", "travel");
    }
}
