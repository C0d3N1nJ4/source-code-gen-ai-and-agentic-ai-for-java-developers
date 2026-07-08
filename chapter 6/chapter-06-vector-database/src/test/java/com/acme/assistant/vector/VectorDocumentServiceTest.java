package com.acme.assistant.vector;

import com.acme.assistant.model.DocumentChunk;
import com.acme.assistant.model.DocumentMetadata;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

@DisplayName("VectorDocumentService Tests")
class VectorDocumentServiceTest {

    @SuppressWarnings("unchecked")
    @Test
    void preservesMetadataWhenPersistingChunks() {
        VectorStore vectorStore = Mockito.mock(VectorStore.class);
        VectorDocumentService service = new VectorDocumentService(vectorStore);

        DocumentChunk chunk = new DocumentChunk(
                "chunk-1",
                "remote-work-policy",
                0,
                "Employees may work remotely up to three days per week.",
                new DocumentMetadata(
                        "Remote Work Policy",
                        "HR",
                        "Remote Work",
                        "acme://policies/hr/remote-work"));

        service.storeChunks(List.of(chunk));

        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(vectorStore).add(captor.capture());

        Document stored = captor.getValue().getFirst();
        Assertions.assertEquals("remote-work-policy", stored.getMetadata().get("documentId"));
        Assertions.assertEquals("HR", stored.getMetadata().get("department"));
        Assertions.assertEquals("Remote Work", stored.getMetadata().get("category"));
    }
}
