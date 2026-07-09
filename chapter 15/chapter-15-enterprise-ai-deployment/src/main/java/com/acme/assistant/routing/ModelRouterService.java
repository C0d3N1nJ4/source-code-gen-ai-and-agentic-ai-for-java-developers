package com.acme.assistant.routing;

import com.acme.assistant.config.AssistantProperties;
import java.util.Locale;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ModelRouterService {

    private final AssistantProperties properties;
    private final Environment environment;

    public ModelRouterService(AssistantProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    public ModelSelection selectModel(String question) {
        String normalized = question == null ? "" : question.toLowerCase(Locale.ROOT);
        AssistantProperties.Routing routing = properties.getRouting();

        if (normalized.contains("holiday") || normalized.contains("hr") || normalized.contains("leave")) {
            return new ModelSelection(routing.getLocalSmallModel(), "small-local", false);
        }
        if (normalized.contains("architecture") || normalized.contains("design") || normalized.contains("trade-off")) {
            return new ModelSelection(routing.getReasoningModel(), "reasoning", false);
        }
        if (normalized.contains("code") || normalized.contains("java") || normalized.contains("api")) {
            return new ModelSelection(routing.getCodingModel(), "coding", false);
        }
        if (normalized.contains("fallback") || normalized.contains("cloud")) {
            boolean openAiProfile = environment.matchesProfiles("openai");
            return new ModelSelection(
                openAiProfile ? routing.getCloudFallbackModel() : routing.getDefaultModel(),
                openAiProfile ? "cloud-fallback" : "default-fallback",
                openAiProfile
            );
        }
        return new ModelSelection(routing.getDefaultModel(), "default", false);
    }
}
