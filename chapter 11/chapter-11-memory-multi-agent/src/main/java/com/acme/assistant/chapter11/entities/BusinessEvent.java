package com.acme.assistant.chapter11.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Captures auditable business events produced by tools and MCP workflows.
 */
@Entity
@Table(name = "business_events", indexes = {
        @Index(name = "idx_business_events_user_created", columnList = "user_id,created_at"),
        @Index(name = "idx_business_events_type_created", columnList = "event_type,created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class BusinessEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "details", nullable = false, length = 4000)
    private String details;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
