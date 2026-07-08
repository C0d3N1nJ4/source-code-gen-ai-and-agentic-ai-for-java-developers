package com.acme.assistant.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acme.assistant.exception.InvalidAiResponseException;
import com.acme.assistant.model.DocumentSummaryResponse;
import jakarta.validation.Validation;
import java.util.List;
import org.junit.jupiter.api.Test;

class AiResponseValidationServiceTest {

    private final AiResponseValidationService validationService =
            new AiResponseValidationService(Validation.buildDefaultValidatorFactory().getValidator());

    @Test
    void validateOrThrow_shouldRejectInvalidStructuredOutput() {
        DocumentSummaryResponse invalid = new DocumentSummaryResponse(
                "",
                "",
                List.of(),
                List.of(""),
                List.of());

        InvalidAiResponseException exception = assertThrows(
                InvalidAiResponseException.class,
                () -> validationService.validateOrThrow(invalid, "document summary"));

        assertTrue(exception.getMessage().contains("document summary"));
    }
}
