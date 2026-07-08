package com.acme.assistant.chapter12.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "audit_events")
public class AuditEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(length = 64)
    private String conversationId;

    @Column(length = 64)
    private String selectedAgent;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(nullable = false, length = 32)
    private String authorizationResult;

    @Column(nullable = false)
    private double promptRiskScore;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private Instant timestamp;
}
