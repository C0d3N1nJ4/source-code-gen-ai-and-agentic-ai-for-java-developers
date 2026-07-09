package com.acme.assistant.security;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    public void log(String userId, String action, String conversationId) {
        log.info("audit event userId={} action={} conversationId={} at={}", userId, action, conversationId, Instant.now());
    }
}
