package com.acme.assistant.chapter12.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.chapter12.config.AiSecurityProperties;
import com.acme.assistant.chapter12.governance.GovernancePolicyService;
import org.junit.jupiter.api.Test;

class GovernancePolicyServiceTest {

    private final GovernancePolicyService service = new GovernancePolicyService(new AiSecurityProperties());

    @Test
    void shouldRequireApprovalForHighExpense() {
        GovernancePolicyService.GovernanceDecision decision = service.evaluate("Please submit expense: 1200 for conference");

        assertThat(decision.requiresApproval()).isTrue();
        assertThat(decision.requiredRole()).isEqualTo("FINANCE");
    }

    @Test
    void shouldRequireAdminForProductionRestart() {
        GovernancePolicyService.GovernanceDecision decision = service.evaluate("restart production server immediately");

        assertThat(decision.requiresApproval()).isTrue();
        assertThat(decision.requiredRole()).isEqualTo("ADMIN");
    }
}
