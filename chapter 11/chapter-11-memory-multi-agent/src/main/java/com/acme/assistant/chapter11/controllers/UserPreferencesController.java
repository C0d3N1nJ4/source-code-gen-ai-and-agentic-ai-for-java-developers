package com.acme.assistant.chapter11.controllers;

import com.acme.assistant.chapter11.dto.UserPreferencesRequest;
import com.acme.assistant.chapter11.dto.UserPreferencesResponse;
import com.acme.assistant.chapter11.services.UserPreferenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Read/write API for user preference memory.
 */
@RestController
public class UserPreferencesController {

    private final UserPreferenceService userPreferenceService;

    public UserPreferencesController(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    @GetMapping("/users/{id}/preferences")
    public UserPreferencesResponse getPreferences(@PathVariable("id") String userId) {
        return userPreferenceService.get(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User preferences not found"));
    }

    @PostMapping("/users/{id}/preferences")
    @ResponseStatus(HttpStatus.CREATED)
    public UserPreferencesResponse upsertPreferences(
            @PathVariable("id") String userId,
            @Valid @RequestBody UserPreferencesRequest request) {
        return userPreferenceService.upsert(userId, request);
    }
}
