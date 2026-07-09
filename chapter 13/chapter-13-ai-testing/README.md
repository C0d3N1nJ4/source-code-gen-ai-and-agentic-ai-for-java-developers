# Chapter 13: Testing AI Applications

This module extends the Chapter 12 secured Acme Corporation Enterprise Assistant and introduces a production-grade AI testing and evaluation framework.

The focus is validating AI behavior, quality, safety, and business outcomes across the full enterprise architecture rather than adding new business capabilities.

Chapter 13 now treats the Chapter 12 security, governance, memory, MCP, tool-calling, and specialist-agent assistant behavior as the baseline that the evaluation layer exercises.

## Project Overview

Chapter 13 provides:

- Layered AI testing from unit to workflow evaluation
- Dataset-driven prompt and RAG regression testing
- Tool calling, MCP, memory, and multi-agent validation suites
- Configurable quality gates enforced during `mvn verify`
- Persisted JSON and HTML quality reports
- Secured evaluation and AI metrics endpoints
- A chapter 12 assistant baseline carried forward into the testing and evaluation layer

## AI Testing Architecture

The testing strategy validates each architectural responsibility independently and then as end-to-end workflows.

```mermaid
flowchart TB

Unit Tests

Integration Tests

Prompt Evaluation

RAG Evaluation

Workflow Tests

End-to-End AI Tests
```

## Continuous AI Evaluation Pipeline

```mermaid
flowchart LR

Developer

Git Commit

Build

Unit Tests

Integration Tests

Prompt Evaluation

RAG Evaluation

Workflow Evaluation

Regression Suite

Deploy
```

## Enterprise AI Evaluation Architecture

```mermaid
flowchart TB

Prompt Evaluation

RAG Evaluation

Tool Calling

MCP

Memory

Agents

Workflow Evaluation

Quality Report
```

## Unit Testing

Unit tests cover deterministic service behavior for prompt scoring, retrieval ranking, tool selection, memory scoring, and workflow orchestration.

## Integration Testing

Integration tests validate:

- Secured endpoint access
- Full AI regression execution and quality gate enforcement
- Testcontainers PostgreSQL + PGVector startup support

## Prompt Evaluation

Prompts are treated as production code with baseline-vs-candidate comparison across enterprise scenarios.

Metrics include:

- accuracy
- relevance
- completeness
- groundedness
- safety
- consistency

## RAG Evaluation

RAG tests validate:

- retrieval relevance and ranking quality
- retrieval latency
- grounding
- hallucination risk detection
- retrieval precision and recall

Pipeline coverage:

Enterprise Documents -> Chunking -> Embeddings -> PGVector -> Similarity Search -> Prompt Construction -> LLM

## Tool Calling Tests

Tool suites validate:

- correct tool selected
- parameter extraction
- authorization enforcement
- business rule outcomes
- failure handling

Example tools:

- submitLeave
- createExpense
- lookupEmployee
- restartServer

## MCP Testing

MCP suites validate:

- discovery
- authentication
- authorization
- capability lookup
- serialization/deserialization
- retries and timeouts
- graceful failures

## Memory Testing

Conversation memory suites validate:

- context retention
- context boundaries
- preference updates
- user isolation
- memory continuity across turns

## Multi-Agent Testing

Coordinator and specialist suites validate:

- planning
- routing
- delegation
- aggregation
- partial failures
- agent isolation boundaries

## Workflow Testing

End-to-end scenarios validate business flow completion across tools, MCP, RAG, memory, and authorization.

## AI Regression Testing

Regression strategy:

- Prompt changes trigger prompt evaluation
- Document changes trigger RAG evaluation
- Tool changes trigger tool-calling suites
- Agent changes trigger workflow suites

Results are persisted to:

- `target/evaluations/latest-results.json`
- `target/evaluations/latest-results.html`

## Quality Gates

Configurable thresholds are defined in `src/main/resources/application.yml`:

- Prompt Accuracy: 95%
- Grounded Responses: 98%
- Tool Selection: 99%
- Workflow Completion: 99%
- Unauthorized Tool Calls: 0
- Hallucination Rate: < 2%

Build fails when thresholds are not met.

## Running Tests

- Unit and AI tests: `mvn test`
- Full verification with regression and integration suites: `mvn verify`

## Running Evaluations

Secured endpoints (Basic Auth default: `evaluator` / `evaluator-pass`):

- `GET /evaluations`
- `POST /evaluations/prompts`
- `POST /evaluations/rag`
- `POST /evaluations/workflows`
- `GET /evaluations/results`
- `GET /metrics/ai`

Legacy chapter compatibility endpoints remain under ` /api/ai-testing/** `.

## Continuous Integration

`mvn verify` runs:

- unit tests
- integration tests
- prompt evaluation tests
- RAG evaluation tests
- workflow evaluation tests
- AI regression suite

## Profiles and Providers

- Local profile (default): Ollama with `llama3.1` and `nomic-embed-text`
- Cloud profile: OpenAI via `application-openai.yml`

## Docker Compose

The local stack includes:

- PostgreSQL + PGVector
- Ollama
- Optional Open WebUI (`--profile ui`)

Run:

`docker compose up -d`

## Testing Assets

Structured testing assets are stored under:

- `src/test/resources/datasets/prompts.json`
- `src/test/resources/datasets/rag.json`
- `src/test/resources/datasets/tool-calling.json`
- `src/test/resources/datasets/memory.json`
- `src/test/resources/datasets/agents.json`
- `src/test/resources/datasets/workflows.json`
