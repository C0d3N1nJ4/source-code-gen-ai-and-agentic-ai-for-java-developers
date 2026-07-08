Task:
Classify the support ticket below and return structured JSON.

Ticket Text:
{ticketText}

Required JSON fields:
- category: string
- priority: one of LOW, MEDIUM, HIGH
- sentiment: one of NEGATIVE, NEUTRAL, POSITIVE
- requiresHumanReview: boolean
- confidence: decimal between 0.0 and 1.0

Constraints:
- Output format preference: {format}
- Category should represent enterprise support routing intent.
- Do not include markdown or extra commentary.
- Return valid JSON only.
