package com.acme.assistant.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class DockerProfileFileTest {

    @Test
    void shouldContainDockerMcpUrl() throws Exception {
        Path file = Path.of("src/main/resources/application-docker.yml");
        String content = Files.readString(file);
        assertThat(content).contains("mock-mcp-server");
    }
}
