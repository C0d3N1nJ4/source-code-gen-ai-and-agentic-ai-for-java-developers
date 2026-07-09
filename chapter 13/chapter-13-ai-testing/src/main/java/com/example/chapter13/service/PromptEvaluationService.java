package com.example.chapter13.service;

import com.example.chapter13.dto.EvaluationRequests.PromptEvaluationRequest;
import com.example.chapter13.dto.EvaluationResponses.PromptEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.ScenarioResult;
import com.example.chapter13.dto.EvaluationResponses.VersionScore;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PromptEvaluationService {

    public PromptEvaluationResponse evaluatePromptVersions(PromptEvaluationRequest request) {
        VersionScore baseline = scoreVersion("baseline", request.baselinePrompt(), request.scenarios());
        VersionScore candidate = scoreVersion("candidate", request.candidatePrompt(), request.scenarios());

        List<ScenarioResult> scenarioResults = new ArrayList<>();
        for (String scenario : request.scenarios()) {
            int baselineScenarioScore = scoreScenario(request.baselinePrompt(), scenario);
            int candidateScenarioScore = scoreScenario(request.candidatePrompt(), scenario);
            String winner = candidateScenarioScore >= baselineScenarioScore ? "candidate" : "baseline";
            String reason = candidateScenarioScore >= baselineScenarioScore
                    ? "Candidate prompt stays more grounded and concise for this business case."
                    : "Baseline prompt remains stronger for this scenario."
                    ;
            scenarioResults.add(new ScenarioResult(scenario, winner, reason));
        }

        Map<String, Integer> qualityDelta = new LinkedHashMap<>();
        qualityDelta.put("accuracy", candidate.accuracy() - baseline.accuracy());
        qualityDelta.put("groundedness", candidate.groundedness() - baseline.groundedness());
        qualityDelta.put("safety", candidate.safety() - baseline.safety());
        qualityDelta.put("conciseness", candidate.conciseness() - baseline.conciseness());
        qualityDelta.put("aggregateScore", candidate.aggregateScore() - baseline.aggregateScore());

        String recommendedVersion = candidate.aggregateScore() >= baseline.aggregateScore() ? "candidate" : "baseline";
        return new PromptEvaluationResponse(
                recommendedVersion,
                List.of(baseline, candidate),
                scenarioResults,
                qualityDelta);
    }

    public PromptEvaluationResponse defaultComparison() {
        return evaluatePromptVersions(new PromptEvaluationRequest(
                "You are an enterprise assistant.",
                "You are an enterprise assistant. Answer only using approved enterprise documentation. Respond concisely. Never speculate.",
                List.of(
                        "How many leave days do I receive?",
                        "Explain the travel reimbursement policy.",
                        "Summarize the API migration strategy.",
                        "What is our password rotation policy?")));
    }

    private VersionScore scoreVersion(String version, String prompt, List<String> scenarios) {
        String normalized = prompt.toLowerCase(Locale.ROOT);
        boolean enterprise = normalized.contains("enterprise");
        boolean documentation = normalized.contains("documentation") || normalized.contains("company policies");
        boolean concise = normalized.contains("concise");
        boolean noSpeculation = normalized.contains("never speculate");
        boolean references = normalized.contains("reference") || normalized.contains("approved");
        boolean confidentiality = normalized.contains("confidential") || normalized.contains("never reveal");

        int scenarioCoverage = scenarios.size() >= 4 ? 4 : scenarios.size();
        int accuracy = clamp(80 + bonus(enterprise, 2) + bonus(documentation, 9) + bonus(noSpeculation, 6) + scenarioCoverage);
        int relevance = clamp(84 + bonus(enterprise, 5) + scenarioCoverage);
        int completeness = clamp(79 + bonus(documentation, 6) + bonus(references, 5) + scenarioCoverage);
        int conciseness = clamp(70 + bonus(concise, 20) + bonus(noSpeculation, 4));
        int groundedness = clamp(76 + bonus(documentation, 14) + bonus(noSpeculation, 8));
        int safety = clamp(80 + bonus(noSpeculation, 9) + bonus(confidentiality, 6) + bonus(documentation, 4));
        int tone = clamp(84 + bonus(enterprise, 4) + bonus(concise, 3));
        int averageResponseLength = Math.max(170, 340 - bonus(concise, 90) - bonus(documentation, 30));
        int aggregate = (accuracy + relevance + completeness + conciseness + groundedness + safety + tone) / 7;

        List<String> strengths = new ArrayList<>();
        if (documentation) {
            strengths.add("Grounds responses in approved documentation");
        }
        if (concise) {
            strengths.add("Reduces verbose responses");
        }
        if (noSpeculation) {
            strengths.add("Suppresses unsupported claims");
        }
        if (strengths.isEmpty()) {
            strengths.add("Provides general enterprise assistant framing");
        }

        return new VersionScore(
                version,
                accuracy,
                relevance,
                completeness,
                conciseness,
                groundedness,
                safety,
                tone,
                averageResponseLength,
                aggregate,
                strengths);
    }

    private int scoreScenario(String prompt, String scenario) {
        String normalizedPrompt = prompt.toLowerCase(Locale.ROOT);
        String normalizedScenario = scenario.toLowerCase(Locale.ROOT);
        int score = 78;
        if (normalizedPrompt.contains("documentation") || normalizedPrompt.contains("approved")) {
            score += 10;
        }
        if (normalizedPrompt.contains("concise")) {
            score += 6;
        }
        if (normalizedPrompt.contains("never speculate")) {
            score += 8;
        }
        if (normalizedScenario.contains("policy") && normalizedPrompt.contains("documentation")) {
            score += 4;
        }
        if (normalizedScenario.contains("api") && normalizedPrompt.contains("enterprise")) {
            score += 2;
        }
        return clamp(score);
    }

    private int bonus(boolean condition, int amount) {
        return condition ? amount : 0;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}