# Advanced Examples

## Run Secured Evaluation Endpoints

Use Basic Auth with the default evaluator account:

- username: evaluator
- password: evaluator-pass

### Prompt Evaluation

curl -u evaluator:evaluator-pass -X POST http://localhost:8080/evaluations/prompts

### RAG Evaluation

curl -u evaluator:evaluator-pass -X POST http://localhost:8080/evaluations/rag

### Workflow Evaluation

curl -u evaluator:evaluator-pass -X POST http://localhost:8080/evaluations/workflows

### Full Results

curl -u evaluator:evaluator-pass http://localhost:8080/evaluations/results

### AI Metrics

curl -u evaluator:evaluator-pass http://localhost:8080/metrics/ai

## Regression Trigger Matrix

- Prompt changes -> run prompt evaluation tests
- Document updates -> run RAG evaluation tests
- Tool changes -> run tool-calling tests
- Agent changes -> run workflow and multi-agent tests
