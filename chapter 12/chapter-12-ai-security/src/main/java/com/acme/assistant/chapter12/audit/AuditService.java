package com.acme.assistant.chapter12.audit;

import com.acme.assistant.chapter12.dto.AuditEventResponse;
import com.acme.assistant.chapter12.entities.AuditEventEntity;
import com.acme.assistant.chapter12.repositories.AuditEventRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    public void record(
            String username,
            String conversationId,
            String selectedAgent,
            String action,
            String authorizationResult,
            double promptRiskScore,
            String details) {
        AuditEventEntity entity = new AuditEventEntity();
        entity.setUsername(username);
        entity.setConversationId(conversationId);
        entity.setSelectedAgent(selectedAgent);
        entity.setAction(action);
        entity.setAuthorizationResult(authorizationResult);
        entity.setPromptRiskScore(promptRiskScore);
        entity.setDetails(details);
        entity.setTimestamp(Instant.now());
        auditEventRepository.save(entity);
    }

    public List<AuditEventResponse> latest() {
        return auditEventRepository.findTop200ByOrderByTimestampDesc().stream()
                .map(event -> new AuditEventResponse(
                        event.getUsername(),
                        event.getConversationId(),
                        event.getSelectedAgent(),
                        event.getAction(),
                        event.getAuthorizationResult(),
                        event.getPromptRiskScore(),
                        event.getDetails(),
                        event.getTimestamp()))
                .toList();
    }
}
