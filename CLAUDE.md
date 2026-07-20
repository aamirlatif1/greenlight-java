# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Status

Early-stage scaffold. The only source file is `src/main/java/com/rev/Main.java`, a placeholder `main` that prints a greeting. The dependencies in `pom.xml` describe the *intended* stack; almost none of it is wired up yet.

## Intended stack

- **Spark Java** (`spark-core`) — HTTP/web layer.
- **jOOQ** (`jooq`, `jooq-meta`, `jooq-codegen`) — typed SQL / DB access. No jOOQ codegen plugin is configured in `pom.xml` yet, so no generated classes exist.
- **PostgreSQL** (`postgresql`, runtime scope) — database.
- **Flyway** (`flyway-maven-plugin`) — schema migrations.

## Build & run

The project targets **Java 25** (`maven.compiler.source/target = 25`), and `Main.java` uses Java 25 language features — an instance `main()` with no `args` and the implicit `IO.println` (JEP 512, compact source files). Building on an older JDK will fail. There is **no Maven wrapper** (`.mvn/` is empty, no `mvnw`), so a system `mvn` on a JDK 25 toolchain is required.

- Build: `mvn compile`
- Package: `mvn package` (artifact `greenlight-1.0-SNAPSHOT`)
- Run: `mvn compile exec:java` (`exec-maven-plugin` is configured with `mainClass = com.rev.Main`)
- Tests: none exist and no test framework is declared. `mvn test` is a no-op until a dependency (e.g. JUnit) and `src/test/java` are added.

## POM plugins

The `<build><plugins>` section contains:

- `flyway-maven-plugin` — migrations (`mvn flyway:migrate`). Its `user`/`password`/`schema` values are still placeholders (`databaseUser`, etc.) and must be set (or overridden with `-Dflyway.user=…`) against a real database.
- `exec-maven-plugin` — runs `com.rev.Main`.

No jOOQ codegen plugin is configured yet; add one under `<build><plugins>` (it needs a live database connection to generate classes).

## Environment note

This directory is **not a git repository** — initialize one before expecting version-control workflows to work.
