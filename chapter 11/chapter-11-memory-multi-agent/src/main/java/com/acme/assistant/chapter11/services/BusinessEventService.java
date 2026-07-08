package com.acme.assistant.chapter11.services;

import com.acme.assistant.chapter11.dto.BusinessEventResponse;
import com.acme.assistant.chapter11.entities.BusinessEvent;
import com.acme.assistant.chapter11.repositories.BusinessEventRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Stores and retrieves business event memory.
 */
@Service
public class BusinessEventService {

    private final BusinessEventRepository businessEventRepository;

    public BusinessEventService(BusinessEventRepository businessEventRepository) {
        this.businessEventRepository = businessEventRepository;
    }

    @Transactional
    public void record(String userId, String conversationId, String eventType, String details) {
        BusinessEvent event = new BusinessEvent();
        event.setUserId(userId);
        event.setConversationId(conversationId);
        event.setEventType(eventType);
        event.setDetails(details);
        event.setCreatedAt(OffsetDateTime.now());
        businessEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<BusinessEventResponse> listAll() {
        return businessEventRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BusinessEventResponse> listForUser(String userId) {
        return businessEventRepository.findTop50ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private BusinessEventResponse toResponse(BusinessEvent event) {
        return new BusinessEventResponse(
                event.getId(),
                event.getUserId(),
                event.getConversationId(),
                event.getEventType(),
                event.getDetails(),
                event.getCreatedAt());
    }
}
