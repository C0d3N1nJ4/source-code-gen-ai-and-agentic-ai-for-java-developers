package com.acme.assistant.chapter12.repositories;

import com.acme.assistant.chapter12.entities.UserPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreferenceEntity, String> {
}
