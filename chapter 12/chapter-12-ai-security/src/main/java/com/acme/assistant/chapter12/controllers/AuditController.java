package com.acme.assistant.chapter12.controllers;

import com.acme.assistant.chapter12.audit.AuditService;
import com.acme.assistant.chapter12.audit.SecurityEventService;
import com.acme.assistant.chapter12.dto.AuditEventResponse;
import com.acme.assistant.chapter12.dto.SecurityEventResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditController {

    private final AuditService auditService;
    private final SecurityEventService securityEventService;

    public AuditController(AuditService auditService, SecurityEventService securityEventService) {
        this.auditService = auditService;
        this.securityEventService = securityEventService;
    }

    @GetMapping("/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditEventResponse> audit() {
        return auditService.latest();
    }

    @GetMapping("/security/events")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SecurityEventResponse> securityEvents() {
        return securityEventService.latest();
    }
}
