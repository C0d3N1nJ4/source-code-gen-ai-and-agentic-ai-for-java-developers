package com.acme.assistant.chapter11.services;

import com.acme.assistant.chapter11.dto.UserPreferencesRequest;
import com.acme.assistant.chapter11.dto.UserPreferencesResponse;
import com.acme.assistant.chapter11.entities.UserPreferenceEntity;
import com.acme.assistant.chapter11.repositories.UserPreferenceRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user preference memory persistence and retrieval.
 */
@Service
public class UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    @Transactional
    public UserPreferencesResponse upsert(String userId, UserPreferencesRequest request) {
        UserPreferenceEntity entity = userPreferenceRepository.findById(userId)
                .orElseGet(UserPreferenceEntity::new);

        entity.setUserId(userId);
        entity.setPreferredProgrammingLanguage(request.preferredProgrammingLanguage());
        entity.setPreferredFramework(request.preferredFramework());
        entity.setPreferredResponseStyle(request.preferredResponseStyle());
        entity.setDepartment(request.department());
        entity.setUpdatedAt(OffsetDateTime.now());

        UserPreferenceEntity saved = userPreferenceRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Optional<UserPreferencesResponse> get(String userId) {
        return userPreferenceRepository.findById(userId).map(this::toResponse);
    }

    private UserPreferencesResponse toResponse(UserPreferenceEntity entity) {
        return new UserPreferencesResponse(
                entity.getUserId(),
                entity.getPreferredProgrammingLanguage(),
                entity.getPreferredFramework(),
                entity.getPreferredResponseStyle(),
                entity.getDepartment(),
                entity.getUpdatedAt());
    }
}
