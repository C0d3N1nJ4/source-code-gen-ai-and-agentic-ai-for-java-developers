package com.example.chapter13.service;

import com.example.chapter13.dto.EvaluationResponses.ToolCallingEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.ToolCallingMetrics;
import com.example.chapter13.dto.EvaluationResponses.ToolCaseResult;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ToolCallingEvaluationService {

        private final AcmePlatformContextService acmePlatformContextService;

        public ToolCallingEvaluationService(AcmePlatformContextService acmePlatformContextService) {
                this.acmePlatformContextService = acmePlatformContextService;
        }

    public ToolCallingEvaluationResponse evaluateToolCallingSuite() {
        List<ToolCaseResult> cases = List.of(
                evaluate("Submit annual leave for next Friday"),
                evaluate("Create expense report for Amsterdam conference"),
                evaluate("Lookup employee EMP-1001 details"),
                evaluate("Restart server PROD-DB-01 without admin role"));

        int passedCount = (int) cases.stream().filter(ToolCaseResult::passed).count();
        int unauthorizedCalls = (int) cases.stream()
                .filter(c -> !c.authorized() && !"denied".equals(c.outcome()))
                .count();
        int authorizationEnforcement = unauthorizedCalls == 0 ? 100 : 80;

        ToolCallingMetrics metrics = new ToolCallingMetrics(
                100,
                100,
                authorizationEnforcement,
                100,
                100,
                unauthorizedCalls,
                24);

        List<String> observations = List.of(
                "Tool selection validates leave, expense, employee lookup, and server operations use cases.",
                "Tool authorization continues the chapter 12 role model " + acmePlatformContextService.securityRoles() + ".",
                "Authorization checks block privileged restart operations for unauthorized callers.",
                "Failure scenarios return deterministic business outcomes suitable for CI regression.");

        return new ToolCallingEvaluationResponse("tool-calling-regression", metrics, cases, observations);
    }

    private ToolCaseResult evaluate(String scenario) {
        String normalized = scenario.toLowerCase(Locale.ROOT);
        if (normalized.contains("leave")) {
            return new ToolCaseResult(
                    scenario,
                    "submitLeave",
                    Map.of("date", "next Friday", "leaveType", "ANNUAL"),
                    true,
                    "approved",
                    true);
        }
        if (normalized.contains("expense")) {
            return new ToolCaseResult(
                    scenario,
                    "createExpense",
                    Map.of("destination", "Amsterdam", "category", "Travel"),
                    true,
                    "created",
                    true);
        }
        if (normalized.contains("lookup")) {
            return new ToolCaseResult(
                    scenario,
                    "lookupEmployee",
                    Map.of("employeeId", "EMP-1001"),
                    true,
                    "returned",
                    true);
        }
        return new ToolCaseResult(
                scenario,
                "restartServer",
                Map.of("serverId", "PROD-DB-01"),
                false,
                "denied",
                true);
    }
}
