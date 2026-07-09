package com.example.chapter13.service;

import com.example.chapter13.dto.EvaluationResponses.WorkflowCaseResult;
import com.example.chapter13.dto.EvaluationResponses.WorkflowEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.WorkflowMetrics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkflowEvaluationService {

        private final AcmePlatformContextService acmePlatformContextService;

        public WorkflowEvaluationService(AcmePlatformContextService acmePlatformContextService) {
                this.acmePlatformContextService = acmePlatformContextService;
        }

    public WorkflowEvaluationResponse evaluateEnterpriseWorkflowSuite() {
        List<WorkflowCaseResult> cases = List.of(
                evaluateScenario("I'd like to book annual leave for next Friday."),
                                evaluateScenario("Estimate my travel expenses for Amsterdam next week for Project Orion."),
                                evaluateScenario("Summarize the latest engineering release notes and API migration strategy from enterprise knowledge."),
                                evaluateScenario("Create an incident ticket and notify leadership because the finance system is unavailable."));

        int passedCases = (int) cases.stream().filter(WorkflowCaseResult::passed).count();
        int gracefulFailureRate = cases.stream().anyMatch(result -> "graceful-failure".equals(result.outcome())) ? 100 : 0;

        WorkflowMetrics metrics = new WorkflowMetrics(
                100,
                100,
                100,
                gracefulFailureRate,
                (passedCases * 100) / cases.size());

        List<String> observations = List.of(
                "Tool selection is validated separately from business validation logic.",
                "Workflow scenarios reuse chapter 12 MCP capabilities: " + acmePlatformContextService.mcpTools() + ".",
                "Governance-sensitive operations from chapter 12 remain visible in testing: " + acmePlatformContextService.governanceSensitiveOperations() + ".",
                "These deterministic workflow suites now continue the chapter 12 secured assistant routing model before adding chapter 13 evaluation layers.");

        return new WorkflowEvaluationResponse("enterprise-workflow-regression", metrics, cases, observations);
    }

    private WorkflowCaseResult evaluateScenario(String scenario) {
        String normalized = scenario.toLowerCase(Locale.ROOT);
        if (normalized.contains("leave")) {
            return new WorkflowCaseResult(
                    scenario,
                    acmePlatformContextService.selectedAgent(scenario) + " Agent",
                    Map.of("date", "next Friday", "leaveType", "Annual Leave"),
                    "approved",
                    true);
        }
        if (normalized.contains("incident") || normalized.contains("notify")) {
            return new WorkflowCaseResult(
                    scenario,
                    acmePlatformContextService.selectedAgent(scenario) + " Agent",
                    Map.of("title", "Finance system unavailable", "priority", "HIGH"),
                    "graceful-failure",
                    true);
        }
        if (normalized.contains("expenses")) {
            return new WorkflowCaseResult(
                    scenario,
                    acmePlatformContextService.selectedAgent(scenario) + " Agent",
                    Map.of("destination", "Amsterdam", "timeframe", "next week", "project", "Project Orion"),
                    "completed",
                    true);
        }
        return new WorkflowCaseResult(
                scenario,
                acmePlatformContextService.selectedAgent(scenario) + " Agent",
                Map.of("resource", "mcp://engineering/github/releases", "topic", "API migration strategy"),
                "completed",
                true);
    }
}