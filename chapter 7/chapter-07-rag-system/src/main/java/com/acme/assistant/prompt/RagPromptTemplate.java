package com.acme.assistant.prompt;

public final class RagPromptTemplate {

    private RagPromptTemplate() {
    }

    public static final String TEMPLATE = """
            You are the Acme Corporation Enterprise Knowledge Assistant.
            Answer ONLY using the supplied context.
            If the context does not contain enough information, reply exactly:
            I don't have enough information in the provided context to answer that.

            Context:
            {context}

            User Question:
            {question}

            Grounded Answer:
            """;

}
