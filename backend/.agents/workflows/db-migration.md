# Database Migration Workflow (Flyway)

## 1. Golden Rules
- **Never edit or delete an existing migration file** once it has been committed or applied—doing so breaks Flyway checksum validation. Always create a new migration file to fix or alter a schema.
- **Hibernate must not manage schemas:** Keep `spring.jpa.hibernate.ddl-auto=validate` so Hibernate verifies entity mappings against Flyway migrations instead of mutating tables.
- **One logical change per migration:** Do not bundle unrelated domain schema changes into a single SQL file.

## 2. File Location & Naming
- **Directory:** `src/main/resources/db/migration/`
- **Format:** `V<version>__<snake_case_description>.sql` (note the **double underscore** `__`)
  - Example: `V1__create_users_table.sql`
  - Example: `V2__add_note_tags_table.sql`
- Always check the latest version number in `db/migration/` before creating a new file.

## 3. PostgreSQL Conventions
- Use `snake_case` for all table names, column names, and constraints.
- Explicitly name constraints and indexes for easier debugging:
  - Primary key: `pk_<table>`
  - Foreign key: `fk_<table>_<referenced_table>`
  - Unique: `uq_<table>_<column>`
  - Index: `idx_<table>_<column>`
- Prefer `TIMESTAMPTZ` (timestamp with time zone) over `TIMESTAMP` for audit fields (`created_at`, `updated_at`).
- Always add indexes on foreign key columns and columns frequently used in `WHERE` / `ORDER BY` clauses.

## 4. Step-by-Step Checklist
1. Check `src/main/resources/db/migration/` for the next version number.
2. Write the new SQL migration file with idempotent/safe DDL where appropriate.
3. Update or create the matching JPA `@Entity` (and composite `@Embeddable`/key class if needed) in `<domain>/<module>/infrastructure/persistence/`.
4. Run `./gradlew test` to verify:
   - Flyway applies the migration cleanly against Testcontainers PostgreSQL.
   - Hibernate `ddl-auto=validate` passes with zero schema mismatch errors.