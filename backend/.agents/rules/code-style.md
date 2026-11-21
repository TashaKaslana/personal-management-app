# Code Style & Conventions

## 1. Dependency Injection & Lombok
- **Always use constructor injection** via Lombok's `@RequiredArgsConstructor` with `private final` fields.
- **Never** use field injection (`@Autowired` on fields).

## 2. Controllers & API Layer
- Keep controllers thin: handle HTTP routing, `@Valid` validation, and response wrapping only. **No business logic** in controllers.
- Always wrap responses using `RestApiResponse<T>` static factory methods (e.g., `RestApiResponse.ok(...)`, `RestApiResponse.created(...)`).
- **Never** expose JPA entities directly in controller requests or responses—always use DTOs.

## 3. DTOs & MapStruct
- Use **Java `record`** or Lombok `@Getter`/`@Builder` for DTOs (e.g., `CreateUserRequest`, `UserDto`).
- Annotate request DTO fields with Jakarta Validation (`@NotBlank`, `@Size`, `@NotNull`).
- **Always use MapStruct** for all entity-to-DTO and DTO-to-entity conversions.
- Place all mappers in `<domain>/<module>/infrastructure/mapstruct/` with `@Mapper(componentModel = "spring")`.
- If no mapper exists for an entity or new DTO, **create or extend the MapStruct mapper**—never write manual mapping logic (`new FooDto(...)`, builder calls, or setter chains) inside services or controllers.

## 4. Service Layer & Transactions
- Place `@Transactional` on the **service layer only**—never on controllers or repositories.
- Default to `@Transactional(readOnly = true)` for reads and `@Transactional` for writes.
- Avoid self-invocation of `@Transactional` methods within the same class.
- Keep external API or storage calls outside of database transactions whenever possible.

## 5. JPA Entities & Repositories
- **Never** use Lombok `@Data` on `@Entity` classes (causes `equals`/`hashCode` and `toString` issues with lazy proxies). Use `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@Builder` instead.
- Default `@ManyToOne` and `@OneToOne` relationships to `FetchType.LAZY`.
- Place composite primary keys in `infrastructure/persistence/key/` implementing `Serializable` with proper `equals`/`hashCode`.
- **Repository Queries:**
  - Use derived query methods for simple lookups (1–2 conditions).
  - Use explicit `@Query` (JPQL) for complex filters, projections, or `JOIN FETCH` to avoid N+1 queries.
  - Use `nativeQuery = true` only for PostgreSQL-specific features.

## 6. Exceptions
- **Create custom domain exceptions** in `<domain>/exception/` for business rule violations and domain-specific errors (e.g., `UserNotFoundException`, `OrderAlreadyCancelledException`).
- **Domain handlers only handle domain exceptions:** Use a scoped `@RestControllerAdvice(basePackages = "com.yourapp.<domain>")` with `@Order(Ordered.HIGHEST_PRECEDENCE)` inside `<domain>/exception/` to handle **only** custom exceptions owned by that domain.
- **Global handler (`core/exception/`) owns framework exceptions + 500 fallback:** Map standard Spring/JPA/validation exceptions (e.g., `OptimisticLockingFailureException` -> 409, `MethodArgumentNotValidException` -> 400) in the root `core/exception/` handler, with `@ExceptionHandler(Exception.class)` returning 500 as the final fallback.
  - If a domain needs custom behavior for a framework exception, catch it in the service layer and rethrow a custom domain exception.
- **Never swallow exceptions silently.**

## 7. Logging policy
- Add logs for operational events and failures that help diagnosis. Do not log every method entry/exit or routine CRUD operation.
- INFO: significant operational milestones; summarize batch jobs with counts and duration.
- WARN: unexpected but recoverable conditions or degraded behavior.
- ERROR: failed operations requiring investigation. Include the exception stack trace when needed to diagnose the failure.
- DEBUG: troubleshooting details. Expected business rejections normally need no log.
- Log an exception once at the layer responsible for handling it. Avoid logging and rethrowing the same exception at every layer.
- Include useful context such as operation, entity ID, and counts.
- Never log passwords, tokens, authorization headers, note content, complete request bodies, or user settings.
- Use Lombok `@Slf4j` and parameterized messages; never use `System.out.println`.

## 8. Naming Conventions
- **Controllers:** `<Entity>Controller` (e.g., `UserController`)
- **Services:** `<Entity>Service` (and `<Entity>ServiceImpl` if using interfaces)
- **DTOs:** `<Action><Entity>Request` / `<Entity>Dto` (e.g., `CreateUserRequest`, `UserDto`)
- **Mappers:** `<Entity>Mapper`
- **Repositories:** `<Entity>Repository`
- **Exceptions & Handlers:** `<Reason>Exception` and `<Domain>SpecificExceptionHandler`

## 9. Imports
- **Never use inline fully qualified names (FQNs)** for classes or annotations (e.g., `@org.hibernate.annotations.DynamicUpdate`). Always add an explicit `import` statement at the top of the file.
- The **only** exception is a genuine simple-name collision within the same file (e.g., `java.util.Date` vs `java.sql.Date`).
- Never use wildcard imports (`import java.util.*;`).