# Acme Enterprise Assistant — Chapter 3

Local learning application, not a production service. Java 25, Gradle Wrapper, Spring Boot 4.2.0 and Spring AI 2.1.0. The default binds to loopback; no enterprise data, tools, memory or business mutations.

From the repository root:

    ./gradlew :acme-enterprise-assistant:build

Default tests mock model boundaries and do not need Ollama or credentials.

## Ollama

Start Ollama, pull llama3.2, then:

    ./gradlew :acme-enterprise-assistant:bootRun

    curl -fsS http://localhost:8080/actuator/health
    curl -i -H 'Content-Type: application/json' -d '{"message":"Explain Java records."}' http://localhost:8080/api/v1/assistant/ask
    curl -N -H 'Content-Type: application/json' -d '{"message":"Explain Java records."}' http://localhost:8080/api/v1/assistant/stream

The ask endpoint returns JSON with an answer. Streaming sends token events then a done event; a provider failure sends an error event instead (HTTP status may already be committed). Invalid input returns 400, provider failures return sanitized 503/504 problems for non-streamed requests. X-Request-Id identifies the HTTP request, not a distributed trace. Local actuator health and metrics are available. No prompt or response logging is added.

## OpenAI alternative

Set OPENAI_API_KEY securely in your environment and OPENAI_MODEL to a supported model. Never commit keys or send sensitive input. Activate the openai profile:

    SPRING_PROFILES_ACTIVE=openai ./gradlew :acme-enterprise-assistant:bootRun

The same service/controller use ChatClient; provider behavior differs. Cloud execution may incur charges. The provider property layout must be verified against the pinned release during smoke testing.

## Opt-in live tests

    ./gradlew :acme-enterprise-assistant:test -PliveModel=true --rerun-tasks

For OpenAI only after setting credentials/model and explicitly accepting costs:

    SPRING_PROFILES_ACTIVE=openai ./gradlew :acme-enterprise-assistant:test -PliveModel=true --rerun-tasks

Tests assert nonempty output, not exact text. Record runtime/model digest and results. ACME_MODEL_TIMEOUT defaults to 60s; ACME_HTTP_ASYNC_TIMEOUT defaults to 75s and should exceed model timeout. The reactive deadline bounds application waiting, not guaranteed provider cancellation or billing. Retries configured to one attempt. Production authentication, rate/concurrency controls and transport-specific timeout tuning are future work.

## Validation status

Offline CI and live smoke results must be recorded before creating v2-chapter-03. No live execution claimed here.
