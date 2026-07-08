package com.acme.assistant.controller;

import com.acme.assistant.model.DocumentIngestionResponse;
import com.acme.assistant.service.DocumentIngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentIngestionService documentIngestionService;

    @Test
    void shouldIngestDocuments() throws Exception {
        when(documentIngestionService.ingest(any())).thenReturn(new DocumentIngestionResponse(2, 8));

        mockMvc.perform(post("/api/documents/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "chunkSize": 200,
                                  "documents": [
                                    {
                                      "title": "Leave Policy",
                                      "source": "hr-policy.md",
                                      "content": "Employees can carry up to 5 leave days.",
                                      "metadata": {
                                        "department": "HR"
                                      }
                                    },
                                    {
                                      "title": "Expense Policy",
                                      "source": "finance-policy.md",
                                      "content": "Hotel expenses require receipts.",
                                      "metadata": {
                                        "department": "Finance"
                                      }
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceDocuments").value(2))
                .andExpect(jsonPath("$.chunksCreated").value(8));
    }
}
