package com.acme.assistant.chapter11.rag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Loads classpath enterprise documents into vector memory at startup.
 */
@Component
public class KnowledgeIngestionRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeIngestionRunner.class);

    private final EnterpriseKnowledgeService enterpriseKnowledgeService;

    public KnowledgeIngestionRunner(EnterpriseKnowledgeService enterpriseKnowledgeService) {
        this.enterpriseKnowledgeService = enterpriseKnowledgeService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Resource> resources = List.of(new PathMatchingResourcePatternResolver()
                .getResources("classpath:knowledge/*.md"));

        for (Resource resource : resources) {
            try {
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                enterpriseKnowledgeService.ingest(resource.getFilename(), content);
            } catch (IOException ex) {
                log.warn("Failed to ingest knowledge resource {}", resource.getFilename(), ex);
            }
        }
        log.info("Knowledge ingestion completed. Resources={}", resources.size());
    }
}
