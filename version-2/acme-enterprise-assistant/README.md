# Acme Enterprise Assistant — Version 2 foundation

Chapter 3 compatibility spike. Java 25, Spring Boot 4.2.0 and Spring AI 2.1.0. This is a local learning application, not a secure production service. It binds to loopback and has no tools, business mutations, retrieval or memory.

## Build and test

Install Java 25 and Maven 3.9.x. Run from this directory:

    mvn -B verify

The MVC tests mock the application service and do not call an AI model. Maven Wrapper generation is pending; do not claim wrapper support yet.

## Optional local smoke test

Install Ollama and start it if needed. Pull a model:

    ollama pull llama3.2
    mvn spring-boot:run

In another terminal:

    curl -fsS http://localhost:8080/actuator/health
    curl -fsS -H 'Content-Type: application/json' -d '{"message":"Explain what a Java record is."}' http://localhost:8080/api/v1/assistant/ask

Expect HTTP 200 and a nonempty answer, not exact wording. Set OLLAMA_BASE_URL and OLLAMA_MODEL to override defaults. Record model digest and runtime version when validating. Stop the application after testing. Never expose this unauthenticated learning endpoint to a network.

## Validation

Official Spring AI release notes confirm the Spring Boot 4.2 baseline: https://spring.io/blog/2026/11/20/spring-ai-2-1-0-GA-available-now/

Compilation, application startup and model execution require recorded evidence before the manuscript baseline is changed. No live inference is run by default. Streaming, provider switching, explicit provider timeouts and error mapping are follow-up Chapter 3 work.
