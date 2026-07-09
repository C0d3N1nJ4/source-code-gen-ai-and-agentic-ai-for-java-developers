package com.acme.assistant.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class ConversationMemoryService {

    private final Map<String, List<String>> messages = new ConcurrentHashMap<>();

    public void append(String conversationId, String role, String content) {
        messages.computeIfAbsent(conversationId, key -> new ArrayList<>())
            .add(role + ":" + content);
    }

    public List<String> history(String conversationId) {
        return List.copyOf(messages.getOrDefault(conversationId, List.of()));
    }
}
