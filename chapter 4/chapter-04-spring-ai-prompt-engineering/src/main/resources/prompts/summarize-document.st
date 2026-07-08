Task:
Summarize the internal document below into structured JSON.

Document Text:
{documentText}

Required JSON fields:
- title: string
- summary: string
- keyPoints: array of strings
- risks: array of strings
- recommendedActions: array of strings

Constraints:
- Output format preference: {format}
- Use only information present in the document text.
- Do not include markdown or extra commentary.
- Return valid JSON only.
