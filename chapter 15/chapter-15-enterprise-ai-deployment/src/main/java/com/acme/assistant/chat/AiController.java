package com.acme.assistant.chat;

import com.acme.assistant.observability.AiHealthIndicator;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AiController {

    private final AssistantService assistantService;
    private final AiHealthIndicator aiHealthIndicator;
    private final MeterRegistry meterRegistry;

    public AiController(AssistantService assistantService, AiHealthIndicator aiHealthIndicator, MeterRegistry meterRegistry) {
        this.assistantService = assistantService;
        this.aiHealthIndicator = aiHealthIndicator;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/chat")
    public AssistantService.ChatResponse chat(@Valid @RequestBody ChatApiRequest request) {
        return assistantService.chat(new AssistantService.ChatRequest(
            request.conversationId(),
            request.userId(),
            request.message(),
            request.context()
        ));
    }

    @PostMapping("/rag/query")
    public AssistantService.RagResponse ragQuery(@Valid @RequestBody RagApiRequest request) {
        return assistantService.ragQuery(new AssistantService.RagQueryRequest(request.query()));
    }

    @PostMapping("/tools/execute")
    public AssistantService.ToolResponse executeTool(@Valid @RequestBody ToolApiRequest request) {
        return assistantService.executeTool(new AssistantService.ToolRequest(request.toolName(), request.arguments()));
    }

    @PostMapping("/agents/run")
    public AssistantService.AgentResponse runAgent(@Valid @RequestBody AgentApiRequest request) {
        return assistantService.runAgent(new AssistantService.AgentRequest(request.instruction()));
    }

    @GetMapping("/health/ai")
    public Map<String, Object> health() {
        return aiHealthIndicator.details();
    }

    @GetMapping("/metrics/ai")
    public Map<String, Double> metrics() {
        List<String> names = List.of(
            "ai.requests.total",
            "ai.tool.calls.total",
            "ai.agent.executions",
            "ai.mcp.calls",
            "ai.fallbacks"
        );
        return names.stream()
            .collect(Collectors.toMap(name -> name, name -> meterRegistry.get(name).counter().count()));
    }

    public record ChatApiRequest(
        @NotBlank String conversationId,
        @NotBlank String userId,
        @NotBlank String message,
        @NotNull Map<String, Object> context
    ) {
    }

    public record RagApiRequest(@NotBlank String query) {
    }

    public record ToolApiRequest(@NotBlank String toolName, @NotNull Map<String, Object> arguments) {
    }

    public record AgentApiRequest(@NotBlank String instruction) {
    }
}
