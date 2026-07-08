# Testing Guide

## Run All Tests

```bash
mvn clean test
```

## Coverage Focus

- Authentication and authorization on REST endpoints
- Prompt injection detection and risk blocking
- Governance policies and human approval workflow triggers
- Tool authorization and business-rule enforcement
- Secure document ingestion and RAG sanitization
- Memory isolation across users and conversations
- Spring Boot context startup and Testcontainers PostgreSQL smoke validation

## Manual Validation Checklist

1. Start infrastructure: postgres and ollama.
2. Start application with JWT-capable security configuration.
3. Call POST /chat with a normal prompt and verify success.
4. Call POST /chat with a malicious prompt (ignore previous instructions) and verify security block message.
5. Call POST /chat with a high-risk action (restart production server) and verify requiresApproval=true.
6. Create a document with trusted metadata via POST /documents.
7. Retrieve documents with GET /documents and verify authorization behavior by role.
8. Attempt cross-user memory access via GET /memory/{conversationId} and verify 403 response.
9. Approve a pending request via POST /approvals/{id}/approve with authorized role.
10. Inspect GET /audit and GET /security/events as ADMIN.
