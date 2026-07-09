package com.acme.assistant.observability;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AiHealthIndicator {

    public Map<String, Object> details() {
        return Map.of(
            "assistant", "ready",
            "routing", "enabled",
            "fallback", "enabled"
        );
    }
}
