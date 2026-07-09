# Testing Guide

## Execute Test Layers

- Unit and AI tests: mvn test
- Full chapter validation: mvn verify

mvn verify executes unit tests, integration tests, AI dataset tests, and the regression quality gate suite.

## Layered Coverage

- Unit: prompt scoring, retrieval ranking, tool selection, workflow logic
- Integration: endpoint security and full-suite orchestration
- Prompt evaluation: baseline vs candidate prompts using enterprise datasets
- RAG evaluation: retrieval precision, recall, groundedness, and latency
- Tool calling: tool choice, parameters, authorization, failures
- MCP: discovery, authN/authZ, capability lookup, retries, timeouts
- Memory: context retention, boundaries, preference updates, isolation
- Agents: planning, routing, delegation, partial-failure handling
- Workflows: end-to-end enterprise completion checks

## Regression Quality Gates

Gates are configured in src/main/resources/application.yml and enforced by integration tests.

- Prompt accuracy
- Grounded responses
- Tool selection accuracy
- Workflow completion rate
- Unauthorized tool calls
- Hallucination rate

## Evaluation Reports

Regression runs persist reports to:

- target/evaluations/latest-results.json
- target/evaluations/latest-results.html
