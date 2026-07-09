package com.example.chapter13.integration;

import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PostgresPgVectorContainerIT {

    @Test
    void shouldStartPgVectorContainerForIntegrationTests() {
        boolean dockerAvailable;
        try {
            dockerAvailable = DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable ex) {
            dockerAvailable = false;
        }
        assumeTrue(dockerAvailable, "Docker is unavailable or incompatible in this environment");

        try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("pgvector/pgvector:pg16"))
                .withDatabaseName("acme")
                .withUsername("acme")
                .withPassword("acme")) {
            postgres.start();
            assertThat(postgres.isRunning()).isTrue();
            assertThat(postgres.getJdbcUrl()).contains("jdbc:postgresql");
        }
    }
}
