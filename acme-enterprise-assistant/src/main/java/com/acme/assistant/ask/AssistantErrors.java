package com.acme.assistant.ask;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AssistantErrors {
    @ExceptionHandler(ModelFailure.class)
    ProblemDetail modelFailure(ModelFailure failure) {
        HttpStatus status = "timeout".equals(failure.code()) ? HttpStatus.GATEWAY_TIMEOUT : HttpStatus.SERVICE_UNAVAILABLE;
        var problem = ProblemDetail.forStatusAndDetail(status, "The assistant could not complete the request.");
        problem.setProperty("code", failure.code());
        return problem;
    }
}
