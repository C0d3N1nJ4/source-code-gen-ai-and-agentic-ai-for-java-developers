package com.acme.assistant.vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

class PgVectorContainerTest {

    @Test
    void shouldStartPostgresContainer() {
        assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker is not available in this environment");
        try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg17")) {
            postgres.start();
            assertThat(postgres.isRunning()).isTrue();
        }
    }
}
