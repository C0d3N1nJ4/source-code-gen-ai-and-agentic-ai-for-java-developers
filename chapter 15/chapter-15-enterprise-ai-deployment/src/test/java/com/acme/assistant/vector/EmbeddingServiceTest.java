package com.acme.assistant.vector;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmbeddingServiceTest {

    @Test
    void shouldGenerateFixedLengthEmbedding() {
        EmbeddingService service = new EmbeddingService();
        assertThat(service.embed("hello")).hasSize(16);
    }
}
