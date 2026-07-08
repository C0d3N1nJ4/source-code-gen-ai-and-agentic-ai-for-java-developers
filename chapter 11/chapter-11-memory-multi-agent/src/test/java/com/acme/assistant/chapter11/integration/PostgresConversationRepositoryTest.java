package com.acme.assistant.chapter11.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.chapter11.entities.ConversationEntity;
import com.acme.assistant.chapter11.repositories.ConversationRepository;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class PostgresConversationRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg17")
            .withDatabaseName("chapter11")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ConversationRepository conversationRepository;

    @MockitoBean
    private ChatModel chatModel;

    @MockitoBean
    private VectorStore vectorStore;

    @MockitoBean
    private EmbeddingModel embeddingModel;

    @Test
    void shouldPersistConversation() {
        ConversationEntity entity = new ConversationEntity();
        entity.setConversationId("conv-db-1");
        entity.setUserId("EMP-DB");
        entity.setCreatedAt(OffsetDateTime.now());

        conversationRepository.save(entity);

        assertThat(conversationRepository.findById("conv-db-1")).isPresent();
    }
}
