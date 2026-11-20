# Architecture & Package Rules

## 1. Top-Level Organization
- **`<base-package>/core/`**: App-wide shared infrastructure (`config/`, `exception/`, `security/`, `shared/`). Never place domain-specific logic here.
- **`<base-package>/<domain>/`**: Independent business domains (e.g., `note/`, `user/`).

## 2. Domain & Sub-Domain Split
Each `<domain>/` is divided into:
- **`<domain>/core/`**: The primary aggregate/entity of the domain (e.g., `note/core/` owns `Note` and its join/supporting entities like `NoteTag` and `NoteTextStyle`).
- **`<domain>/<sub-domain>/`**: Dependent or supporting entities (1-to-N or N-to-N) closely tied to this domain (e.g., `note/tag/`).

## 3. Standard Internal Layout
Both `<domain>/core/` and every `<domain>/<sub-domain>/` follow the exact same package structure:

```text
<domain>/<core | sub-domain>/
├── controller/            # REST endpoints and request validation
├── dtos/                  # Request/response payloads and cross-domain DTOs
├── enums/                 # Domain-specific enums
├── exception/             # Domain-specific exceptions and the handler
├── service/               # Business logic and public module facade
└── infrastructure/        # Private technical adapters for this module ONLY
    ├── mapstruct/         # MapStruct mappers (e.g., NoteMapper)
    ├── persistence/       # Database layer
    │   ├── entities/      # JPA entities (e.g., Note, NoteTag)
    │   ├── key/           # Composite primary keys (e.g., NoteTagId)
    │   └── repositories/  # Spring Data repositories (e.g., NoteRepository)
    └── [storage|client]/  # (Optional) External APIs, S3/Cloudinary, or other external services
```

## 4. Dependency & Boundary Rules
1. **Core isolation:** Modules may depend on `core/`, but `core/` must never depend on any domain module unless has a valid reason.
2. **No cross-module data access:** Module A must never inject Module B's `Repository` or reference its `Entity` classes (avoid JPA `@ManyToOne` across modules; store the foreign ID instead).
3. **Cross-module communication:**
   - **Synchronous:** Call Module B's public `Service` (or a dedicated `api/` interface) using DTOs—never entities.
   - **Asynchronous:** Publish domain events (e.g., `OrderCreatedEvent`) via `ApplicationEventPublisher` for decoupled side effects like notifications.