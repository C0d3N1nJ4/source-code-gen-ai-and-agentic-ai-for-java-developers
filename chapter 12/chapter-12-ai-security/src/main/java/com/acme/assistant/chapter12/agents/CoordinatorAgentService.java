package com.acme.assistant.chapter12.agents;

import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class CoordinatorAgentService {

    public String selectAgent(String message) {
        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("leave") || normalized.contains("employee")) {
            return "HR";
        }
        if (normalized.contains("expense") || normalized.contains("invoice") || normalized.contains("budget")) {
            return "FINANCE";
        }
        if (normalized.contains("server") || normalized.contains("deployment") || normalized.contains("restart")) {
            return "OPERATIONS";
        }
        return "KNOWLEDGE";
    }
}
