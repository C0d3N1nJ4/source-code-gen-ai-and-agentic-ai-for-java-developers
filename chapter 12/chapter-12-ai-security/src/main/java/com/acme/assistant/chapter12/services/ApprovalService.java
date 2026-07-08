package com.acme.assistant.chapter12.services;

import com.acme.assistant.chapter12.dto.ApprovalResponse;
import com.acme.assistant.chapter12.entities.ApprovalRequestEntity;
import com.acme.assistant.chapter12.exceptions.ForbiddenException;
import com.acme.assistant.chapter12.exceptions.NotFoundException;
import com.acme.assistant.chapter12.repositories.ApprovalRequestRepository;
import com.acme.assistant.chapter12.security.AuthorizationService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ApprovalService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final AuthorizationService authorizationService;

    public ApprovalService(ApprovalRequestRepository approvalRequestRepository, AuthorizationService authorizationService) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.authorizationService = authorizationService;
    }

    public ApprovalResponse createApproval(String operation, String target, String requiredRole, String payload) {
        ApprovalRequestEntity entity = new ApprovalRequestEntity();
        entity.setId("APR-" + UUID.randomUUID());
        entity.setOperation(operation);
        entity.setTarget(target);
        entity.setRequestedBy(authorizationService.currentUser());
        entity.setRequiredRole(requiredRole);
        entity.setStatus("PENDING");
        entity.setPayloadJson(payload);
        entity.setCreatedAt(Instant.now());
        return toResponse(approvalRequestRepository.save(entity));
    }

    public List<ApprovalResponse> listApprovals() {
        return approvalRequestRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ApprovalResponse approve(String id) {
        ApprovalRequestEntity entity = approvalRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Approval request not found: " + id));

        if (!authorizationService.hasRole(entity.getRequiredRole()) && !authorizationService.hasRole("ADMIN")) {
            throw new ForbiddenException("Role " + entity.getRequiredRole() + " is required to approve this request");
        }

        entity.setStatus("APPROVED");
        entity.setDecidedAt(Instant.now());
        entity.setDecidedBy(authorizationService.currentUser());
        return toResponse(approvalRequestRepository.save(entity));
    }

    private ApprovalResponse toResponse(ApprovalRequestEntity entity) {
        return new ApprovalResponse(
                entity.getId(),
                entity.getOperation(),
                entity.getTarget(),
                entity.getRequestedBy(),
                entity.getStatus(),
                entity.getRequiredRole(),
                entity.getCreatedAt(),
                entity.getDecidedAt(),
                entity.getDecidedBy());
    }
}
