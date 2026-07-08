package com.acme.assistant.chapter11.controllers;

import com.acme.assistant.chapter11.dto.AgentsResponse;
import com.acme.assistant.chapter11.services.AgentCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read API for specialist agent catalog.
 */
@RestController
public class AgentController {

    private final AgentCatalogService agentCatalogService;

    public AgentController(AgentCatalogService agentCatalogService) {
        this.agentCatalogService = agentCatalogService;
    }

    @GetMapping("/agents")
    public AgentsResponse agents() {
        return agentCatalogService.listAgents();
    }
}
