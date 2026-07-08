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
 * Stores full conversation turn history.
 */
@Entity
@Table(name = "conversation_messages", indexes = {
        @Index(name = "idx_conv_msg_conversation", columnList = "conversation_id,created_at"),
        @Index(name = "idx_conv_msg_user", columnList = "user_id,created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class ConversationMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "content", nullable = false, length = 8000)
    private String content;

    @Column(name = "estimated_tokens", nullable = false)
    private int estimatedTokens;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
