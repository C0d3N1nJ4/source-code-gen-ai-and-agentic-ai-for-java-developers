package com.acme.assistant.document;

import com.acme.assistant.model.DocumentChunk;
import com.acme.assistant.model.PolicyDocument;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DocumentChunkingService Tests")
class DocumentChunkingServiceTest {

    @Test
    void splitsDocumentByParagraphsAndPreservesMetadata() {
        DocumentChunkingService service = new DocumentChunkingService();
        PolicyDocument policyDocument = new PolicyDocument(
                "remote-work",
                "Remote Work Policy",
                "Employees may work remotely up to three days per week.\n\n"
                        + "Manager approval is required for recurring remote schedules.\n\n"
                        + "Remote employees must remain reachable during core collaboration hours.",
                Map.of("department", "HR", "policyType", "remote-work"));

        List<DocumentChunk> chunks = service.chunkDocument(policyDocument, 140, 30);

        Assertions.assertFalse(chunks.isEmpty());
        Assertions.assertEquals("remote-work", chunks.getFirst().documentId());
        Assertions.assertEquals("HR", chunks.getFirst().metadata().get("department"));
        Assertions.assertNotNull(chunks.getFirst().metadata().get("chunkingStrategy"));
    }
}
