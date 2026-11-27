# Chapter 3 — Spring AI fundamentals

This standalone command-line example demonstrates a single ChatClient call without the Acme REST application. From version-2, use Java 25 and Gradle 9.1.0.

```bash
gradle :examples:chapter-03-spring-ai:build
```

Tests mock the model client and need no Ollama or cloud credentials. For a live demonstration, start Ollama, pull llama3.2 and run:

```bash
ollama pull llama3.2
gradle :examples:chapter-03-spring-ai:bootRun
```

The example prints a generated explanation and exits. Model text may vary. Override OLLAMA_BASE_URL or OLLAMA_MODEL if needed. The version catalog supplies version management; keep the version-2 directory intact when cloning. No dependency on the Acme module.
