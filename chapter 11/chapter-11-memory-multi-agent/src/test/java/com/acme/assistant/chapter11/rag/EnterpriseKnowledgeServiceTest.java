package com.acme.assistant.chapter11.rag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

class EnterpriseKnowledgeServiceTest {

    @Test
    void shouldReturnRetrievedSnippets() {
        VectorStore vectorStore = org.mockito.Mockito.mock(VectorStore.class);
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(new Document("Policy snippet")));

        EnterpriseKnowledgeService service = new EnterpriseKnowledgeService(vectorStore);
        List<String> snippets = service.retrieve("policy", 2);

        assertThat(snippets).containsExactly("Policy snippet");
    }
}
