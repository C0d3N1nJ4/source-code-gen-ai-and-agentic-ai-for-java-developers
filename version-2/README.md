# Version 2 companion projects

Hybrid Gradle Kotlin DSL build. Independent chapter examples live in examples/; Acme evolves separately. Both inherit dependency versions from the version catalog. Original edition projects remain untouched.

## Build and run

Use Java 25. The committed Gradle Wrapper downloads Gradle 9.1.0. From version-2:

    ./gradlew clean build
    ./gradlew :examples:chapter-03-spring-ai:build
    ./gradlew :acme-enterprise-assistant:build

For local model exercises, start Ollama and pull llama3.2 first.

    ./gradlew :examples:chapter-03-spring-ai:bootRun
    ./gradlew :acme-enterprise-assistant:bootRun

Default tests do not require live models. Use ./gradlew on macOS/Linux and gradlew.bat on Windows. The distribution checksum is pinned; CI validates the wrapper JAR. Shared Java and Spring AI conventions live in build-logic.

Chapter source checkpoints use v2-chapter-NN tags after successful validation; no tags created yet. Manuscript snippets reference immutable commit SHAs. See individual module READMEs. The initial Gradle build passed CI; the new wrapper and convention plugins require a fresh CI run. Live inference remains unverified.
