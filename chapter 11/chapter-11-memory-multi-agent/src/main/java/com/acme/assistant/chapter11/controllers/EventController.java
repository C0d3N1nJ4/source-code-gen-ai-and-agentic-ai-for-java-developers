package com.acme.assistant.chapter11.controllers;

import com.acme.assistant.chapter11.dto.BusinessEventResponse;
import com.acme.assistant.chapter11.services.BusinessEventService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read API for business event memory.
 */
@RestController
public class EventController {

    private final BusinessEventService businessEventService;

    public EventController(BusinessEventService businessEventService) {
        this.businessEventService = businessEventService;
    }

    @GetMapping("/events")
    public List<BusinessEventResponse> events(@RequestParam(name = "userId", required = false) String userId) {
        if (userId == null || userId.isBlank()) {
            return businessEventService.listAll();
        }
        return businessEventService.listForUser(userId);
    }
}
