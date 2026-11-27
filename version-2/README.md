# Version 2 — Companion projects

This edition uses a hybrid Maven reactor. Each implementation chapter may introduce a focused, independently runnable example under examples/. Acme is a separate evolving reference application that applies the concepts together. Examples do not depend on Acme or on earlier example modules. Do not duplicate the entire Acme application per chapter. Conceptual chapters use design exercises rather than empty modules.

## Current modules

| Module | Purpose |
| --- | --- |
| [Chapter 3 — Spring AI](examples/chapter-03-spring-ai/README.md) | Minimal command-line ChatClient example |
| [Acme Enterprise Assistant](acme-enterprise-assistant/README.md) | Validated REST contract and model integration foundation |

## Build

Use Java 25 and Maven 3.9.x. From version-2:

```bash
mvn -B verify
mvn -B -pl examples/chapter-03-spring-ai verify
mvn -B -pl acme-enterprise-assistant verify
```

Each module also supports mvn verify from its own directory. Live model examples are manual and not executed in the default build. The parent manages Spring Boot and Spring AI versions. Maven Wrapper and real model smoke evidence are still pending.

## Growth strategy

Add independent chapter modules when their learning objectives require code. The Acme application remains modular by capability; introduce a separate MCP server module only when the integration chapter requires it. Use immutable chapter checkpoints for Acme; examples remain available on main. Preserve the original edition's root-level chapter directories. No chapter checkpoint is published until validation passes.

## Validation status

This is a proposed foundation. Compilation and live model execution must be verified before manuscript claims are updated.
