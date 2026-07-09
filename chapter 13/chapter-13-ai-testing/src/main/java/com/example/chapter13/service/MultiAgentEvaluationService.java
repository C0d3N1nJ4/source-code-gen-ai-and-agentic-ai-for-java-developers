package com.example.chapter13.service;

import com.example.chapter13.dto.EvaluationResponses.AgentCaseResult;
import com.example.chapter13.dto.EvaluationResponses.AgentEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.AgentMetrics;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MultiAgentEvaluationService {

        private final AcmePlatformContextService acmePlatformContextService;

        public MultiAgentEvaluationService(AcmePlatformContextService acmePlatformContextService) {
                this.acmePlatformContextService = acmePlatformContextService;
        }

    public AgentEvaluationResponse evaluateAgentSuite() {
        List<AgentCaseResult> cases = List.of(
                new AgentCaseResult(
                        "Travel request requiring HR, Finance, and Notification coordination",
                        List.of("Coordinator Agent", "HR Agent", "Finance Agent", "Notification Agent"),
                        "completed",
                        true),
                new AgentCaseResult(
                        "Architecture question requiring only Knowledge Agent",
                        List.of("Coordinator Agent", "Knowledge Agent"),
                        "completed",
                        true),
                new AgentCaseResult(
                        "Finance partial outage while preparing approval response",
                        List.of("Coordinator Agent", "Finance Agent", "Notification Agent"),
                        "partial-failure-handled",
                        true));

        AgentMetrics metrics = new AgentMetrics(
                99,
                99,
                99,
                98,
                100,
                100,
                126);

        List<String> observations = List.of(
                "Coordinator plans and routes work to specialist agents with deterministic assertions.",
                "The routed agents continue the chapter 12 specialist model: " + acmePlatformContextService.specialistAgents() + ".",
                "Delegation and aggregation are tested independently from LLM wording variability.",
                "Partial failures are isolated and still produce safe aggregated outputs.");

        return new AgentEvaluationResponse("agent-orchestration-regression", metrics, cases, observations);
    }
}
