package com.acme.assistant.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StructuredLoggingService {

    private static final Logger log = LoggerFactory.getLogger(StructuredLoggingService.class);

    public void logAiRequest(String requestId,
                             String userId,
                             String conversationId,
                             String selectedModel,
                             String agent,
                             long latencyMs,
                             int toolCalls,
                             int mcpCalls,
                             boolean fallbackUsed) {
        log.info("requestId={} userId={} conversationId={} model={} agent={} latencyMs={} toolCalls={} mcpCalls={} fallbackUsed={}",
            requestId, userId, conversationId, selectedModel, agent, latencyMs, toolCalls, mcpCalls, fallbackUsed);
    }
}
