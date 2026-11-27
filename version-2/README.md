# Version 2 companion projects

Hybrid Gradle Kotlin DSL build. Independent chapter examples live in examples/; Acme evolves separately. Both inherit dependency versions from the version catalog. Original edition projects remain untouched.

## Build and run

Use Java 25 and Gradle 9.1.0 (initial compatibility candidate). From version-2:

    gradle clean build
    gradle :examples:chapter-03-spring-ai:build
    gradle :acme-enterprise-assistant:build

For local model exercises, start Ollama and pull llama3.2 first.

    gradle :examples:chapter-03-spring-ai:bootRun
    gradle :acme-enterprise-assistant:bootRun

Default tests do not require live models. Generate the standard Gradle Wrapper with gradle wrapper --gradle-version 9.1.0 and commit the generated scripts, JAR and checksum-pinned properties after validation. Until then use installed Gradle; wrapper is not yet included. Shared build configuration is centralized in the root script; extract convention plugins as additional modules justify it.

Chapter source checkpoints use v2-chapter-NN tags after successful validation; no tags created yet. Manuscript snippets reference immutable commit SHAs. See individual module READMEs. Build compatibility and live inference remain unverified.
