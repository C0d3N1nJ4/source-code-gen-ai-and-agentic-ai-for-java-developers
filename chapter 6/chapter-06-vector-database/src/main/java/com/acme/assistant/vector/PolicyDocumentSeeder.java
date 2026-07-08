package com.acme.assistant.vector;

import com.acme.assistant.model.DocumentIngestionRequest;
import com.acme.assistant.service.DocumentIngestionService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
public class PolicyDocumentSeeder implements ApplicationRunner {

    private final DocumentIngestionService ingestionService;

    public PolicyDocumentSeeder(DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    public void run(ApplicationArguments args) {
        ingestionService.ingest(new DocumentIngestionRequest(null, null, null));
    }
}
