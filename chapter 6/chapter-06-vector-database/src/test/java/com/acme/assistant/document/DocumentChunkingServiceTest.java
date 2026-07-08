package com.acme.assistant.document;

import com.acme.assistant.model.DocumentChunk;
import com.acme.assistant.model.PolicyDocumentPayload;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DocumentChunkingService Tests")
class DocumentChunkingServiceTest {

    @Test
    void splitsPolicyByParagraphsWithOverlap() {
        DocumentChunkingService service = new DocumentChunkingService();
        PolicyDocumentPayload payload = new PolicyDocumentPayload(
                "remote-work",
                "Remote Work Policy",
                "Employees may work remotely up to three days per week.\n\n"
                        + "Manager approval is required for recurring remote work.\n\n"
                        + "Employees must remain available during core business hours.\n\n"
                        + "International travel requires additional security controls.",
                "HR",
                "Remote Work",
                "acme://policies/hr/remote-work");

        List<DocumentChunk> chunks = service.chunkDocument(payload, 200, 50);

        Assertions.assertTrue(chunks.size() >= 2);
        Assertions.assertEquals("remote-work", chunks.getFirst().documentId());
        Assertions.assertEquals("HR", chunks.getFirst().metadata().department());
    }
}
