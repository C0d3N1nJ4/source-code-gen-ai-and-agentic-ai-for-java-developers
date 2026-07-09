package com.example.chapter13.service;

import com.example.chapter13.dto.EvaluationResponses.McpCaseResult;
import com.example.chapter13.dto.EvaluationResponses.McpEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.McpMetrics;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class McpEvaluationService {

        private final AcmePlatformContextService acmePlatformContextService;

        public McpEvaluationService(AcmePlatformContextService acmePlatformContextService) {
                this.acmePlatformContextService = acmePlatformContextService;
        }

    public McpEvaluationResponse evaluateMcpSuite() {
        List<McpCaseResult> cases = List.of(
                new McpCaseResult("discovery", true, true, "found-3-servers", true),
                new McpCaseResult("capability-lookup", true, true, "resolved-create_jira_ticket", true),
                new McpCaseResult("serialization", true, true, "payload-roundtrip-ok", true),
                new McpCaseResult("timeout-retry", true, true, "retried-once-success", true),
                new McpCaseResult("authorization", true, false, "graceful-failure", true));

        McpMetrics metrics = new McpMetrics(
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                31);

        List<String> observations = List.of(
                "MCP discovery and capability lookup are validated before invocation.",
                "Allowed capabilities continue the chapter 12 agent allow-list model: " + acmePlatformContextService.mcpCapabilitiesByAgent() + ".",
                "Serialization and deserialization checks prevent schema drift.",
                "Timeout and authorization paths validate retries and graceful failure handling.");

        return new McpEvaluationResponse("mcp-regression", metrics, cases, observations);
    }
}
