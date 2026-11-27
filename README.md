# Version 2 companion projects

Hybrid Gradle Kotlin DSL build. Independent chapter examples live in examples/; Acme evolves separately. Both inherit dependency versions from the version catalog. The first edition is preserved on the [version-1 branch](https://github.com/C0d3N1nJ4/source-code-gen-ai-and-agentic-ai-for-java-developers/tree/version-1).

## Build and run

Use Java 25. The committed Gradle Wrapper downloads Gradle 9.1.0. From the repository root:

    ./gradlew clean build
    ./gradlew :examples:chapter-03-spring-ai:build
    ./gradlew :acme-enterprise-assistant:build

For local model exercises, start Ollama and pull llama3.2 first.

    ./gradlew :examples:chapter-03-spring-ai:bootRun
    ./gradlew :acme-enterprise-assistant:bootRun

Default tests do not require live models. Use ./gradlew on macOS/Linux and gradlew.bat on Windows. The distribution checksum is pinned; CI validates the wrapper JAR. Shared Java and Spring AI conventions live in build-logic.

Chapter source checkpoints use v2-chapter-NN tags after successful validation; no tags created yet. Manuscript snippets reference immutable commit SHAs. See individual module READMEs. The Chapter 3 build passed CI before the repository layout migration; the root layout requires fresh validation. Live inference remains unverified.

## Editions

The default main branch is the second-edition development line. First-edition readers should use the [version-1 branch](https://github.com/C0d3N1nJ4/source-code-gen-ai-and-agentic-ai-for-java-developers/tree/version-1). Chapter checkpoints are published only after validation; no release tags have been created by this migration.
