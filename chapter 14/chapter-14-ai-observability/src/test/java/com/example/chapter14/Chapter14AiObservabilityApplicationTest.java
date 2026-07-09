package com.example.chapter14;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.health.DataSourceHealthContributorAutoConfiguration,org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration",
    "management.health.db.enabled=false"
})
class Chapter14AiObservabilityApplicationTest {

    @MockitoBean
    private ChatModel chatModel;

    @Test
    void contextLoads() {
    }
}