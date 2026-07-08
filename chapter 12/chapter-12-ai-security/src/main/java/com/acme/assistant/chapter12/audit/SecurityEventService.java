package com.acme.assistant.chapter12.audit;

import com.acme.assistant.chapter12.dto.SecurityEventResponse;
import com.acme.assistant.chapter12.entities.SecurityEventEntity;
import com.acme.assistant.chapter12.repositories.SecurityEventRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SecurityEventService {

    private final SecurityEventRepository securityEventRepository;

    public SecurityEventService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    public void record(String username, String eventType, String severity, String details) {
        SecurityEventEntity entity = new SecurityEventEntity();
        entity.setUsername(username);
        entity.setEventType(eventType);
        entity.setSeverity(severity);
        entity.setDetails(details);
        entity.setTimestamp(Instant.now());
        securityEventRepository.save(entity);
    }

    public List<SecurityEventResponse> latest() {
        return securityEventRepository.findTop200ByOrderByTimestampDesc().stream()
                .map(event -> new SecurityEventResponse(
                        event.getUsername(), event.getEventType(), event.getSeverity(), event.getDetails(), event.getTimestamp()))
                .toList();
    }
}
