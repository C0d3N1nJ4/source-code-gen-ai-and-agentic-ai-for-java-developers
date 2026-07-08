# Chapter 3 - OpenAI Cloud (Spring AI Fundamentals)

This project is the cloud provider implementation for Chapter 3: Getting Started with Spring AI.

It demonstrates how Java developers can build an AI-powered Spring Boot application using:
- Spring AI fundamentals
- `ChatModel`, `Prompt`, and `ChatResponse`
- Dependency injection and auto-configuration
- A thin REST controller with service-layer AI interaction
- Basic validation, exception handling, and Actuator health checks

## Why This Project Exists

`chapter-03-openai-cloud` is the cloud-first counterpart to `chapter-03-ollama-local`.

The service layer is intentionally the same architecture and behavior as the Ollama project. Only dependency and configuration change.

## Provider

- Provider: OpenAI (cloud)
- API key required: Yes (`OPENAI_API_KEY`)
- Default model: `gpt-4o-mini` (configurable)

## Prerequisites

- Java 25
- Maven 3.9+
- OpenAI API key

## Environment Setup

Set API key:

```bash
export OPENAI_API_KEY=your_api_key_here
```

Optional model and temperature overrides:

```bash
export OPENAI_CHAT_MODEL=gpt-4o-mini
export OPENAI_TEMPERATURE=0.2
```

## Run The Application

```bash
mvn clean test
mvn spring-boot:run
```

## Configuration

`src/main/resources/application.yml`:

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: ${OPENAI_CHAT_MODEL:gpt-4o-mini}
          temperature: ${OPENAI_TEMPERATURE:0.2}
```

## API

Endpoint:

```http
POST /api/chat
Content-Type: application/json
```

Request:

```json
{
  "message": "Explain dependency injection in Spring Boot."
}
```

Response (example):

```json
{
  "response": "Dependency injection lets Spring supply dependencies from the IoC container, reducing coupling and improving testability."
}
```

## Architecture

```mermaid
flowchart LR

Client[HTTP Client]
-->App[Spring Boot Application]

App
-->SpringAI[Spring AI ChatModel]

SpringAI
-->OpenAI[OpenAI API]

OpenAI
-->CloudModel[Cloud Model]
```

## Cloud Trade-Offs

- Pros: managed scalability, no local model runtime maintenance, easy access to strong hosted models
- Cons: requires API key, token-based cost, dependency on external provider availability/network

## Chapter 3 Alignment

This project intentionally excludes Chapter 4+ topics such as prompt templates, structured outputs, embeddings, RAG, agents, tool calling, MCP, memory, and multi-agent workflows.
