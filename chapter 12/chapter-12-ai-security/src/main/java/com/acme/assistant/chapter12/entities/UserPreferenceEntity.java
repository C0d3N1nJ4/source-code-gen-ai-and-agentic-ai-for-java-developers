package com.acme.assistant.chapter12.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_preferences")
public class UserPreferenceEntity {

    @Id
    @Column(length = 100)
    private String userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String preferencesJson;
}
