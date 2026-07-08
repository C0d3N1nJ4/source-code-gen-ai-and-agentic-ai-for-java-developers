package com.acme.assistant.chapter11.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stores explicit user preference memory independently of chat history.
 */
@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
public class UserPreferenceEntity {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "preferred_programming_language", nullable = false)
    private String preferredProgrammingLanguage;

    @Column(name = "preferred_framework", nullable = false)
    private String preferredFramework;

    @Column(name = "preferred_response_style", nullable = false)
    private String preferredResponseStyle;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
