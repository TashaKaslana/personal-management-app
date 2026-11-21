# AGENTS.md

## 1. Project Overview
- **Purpose:** Personal note management backend application.
- **Stack:** Java 17, Spring Boot 4.1.1, Gradle, PostgreSQL.
- **Core Libraries:** Spring Data JPA, Flyway, Spring Security, JJWT (`0.12.6`), Spring WebMVC, Jakarta Validation, 
Quartz, Spring Mail, Lombok, MapStruct (`1.6.3`), Testcontainers (PostgreSQL).

## 2. Repo Layout
- **Architecture:** Modular Monolith (package-by-feature)
- **Base package:** `com.example.personal_management_app`
- `src/main/java/com/example/personal_management_app/`
  - `core/` — Shared app-wide infrastructure (`config/`, `exception/`, etc.)
  - `<domain>/` — Independent domain modules (e.g., `user/`, `note/`)
- `src/main/resources/` — `application.properties`, DB migrations (`db/migration/`)
- `src/test/java/com/example/personal_management_app/` — Mirrors `main/` structure

## 3. Core Commands
Always verify changes using these commands:
- **Build:** `./gradlew build`
- **Test:** `./gradlew test`
- **Lint / Format:** `./gradlew check`
- **Dev Server:** `./gradlew bootRun`

## 4. Global Guardrails (Always Follow)
- Never read, print, or commit `.env` files or secrets.
- Do not modify generated files or lockfiles manually.
- Never import classes from another domain's `infrastructure/` package.
- Always run the relevant test suite after making code changes.
- Ask before introducing new third-party dependencies.
- 
## 5. Modular Documentation (Read On Demand)
Before starting a task, read the relevant files below:

| If your task involves... | Read first |
| :--- | :--- |
| Writing, refactoring, or scaffolding code | `.agents/rules/architecture.md`, `.agents/rules/code-style.md` |
| Adding or updating tests | `.agents/rules/testing.md` |
| Changing the database schema or entities | `.agents/workflows/db-migration.md` ||
