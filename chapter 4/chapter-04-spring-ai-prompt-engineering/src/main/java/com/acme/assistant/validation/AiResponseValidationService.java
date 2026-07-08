package com.acme.assistant.validation;

import com.acme.assistant.exception.InvalidAiResponseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AiResponseValidationService {

    private final Validator validator;

    public AiResponseValidationService(Validator validator) {
        this.validator = validator;
    }

    public <T> T validateOrThrow(T response, String context) {
        Set<ConstraintViolation<T>> violations = validator.validate(response);
        if (violations.isEmpty()) {
            return response;
        }

        String message = violations.stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .collect(Collectors.joining(", "));
        throw new InvalidAiResponseException("Invalid AI response for " + context + ": " + message);
    }
}
