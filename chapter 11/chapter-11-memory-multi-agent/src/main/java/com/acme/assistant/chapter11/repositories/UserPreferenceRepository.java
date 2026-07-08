package com.acme.assistant.chapter11.repositories;

import com.acme.assistant.chapter11.entities.UserPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for user preference memory.
 */
public interface UserPreferenceRepository extends JpaRepository<UserPreferenceEntity, String> {
}
