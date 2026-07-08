package com.acme.assistant.chapter12.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "approval_requests")
public class ApprovalRequestEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 80)
    private String operation;

    @Column(nullable = false, length = 120)
    private String target;

    @Column(nullable = false, length = 100)
    private String requestedBy;

    @Column(nullable = false, length = 32)
    private String requiredRole;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant decidedAt;

    @Column(length = 100)
    private String decidedBy;
}
