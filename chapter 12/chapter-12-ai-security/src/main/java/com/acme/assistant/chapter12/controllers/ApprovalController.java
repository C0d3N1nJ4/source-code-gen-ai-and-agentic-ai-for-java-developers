package com.acme.assistant.chapter12.controllers;

import com.acme.assistant.chapter12.dto.ApprovalDecisionRequest;
import com.acme.assistant.chapter12.dto.ApprovalResponse;
import com.acme.assistant.chapter12.services.ApprovalService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/approvals")
    @PreAuthorize("hasAnyRole('HR','FINANCE','OPERATIONS','ADMIN')")
    public List<ApprovalResponse> approvals() {
        return approvalService.listApprovals();
    }

    @PostMapping("/approvals/{id}/approve")
    @PreAuthorize("hasAnyRole('HR','FINANCE','ADMIN')")
    public ApprovalResponse approve(@PathVariable String id, @RequestBody(required = false) ApprovalDecisionRequest request) {
        return approvalService.approve(id);
    }
}
