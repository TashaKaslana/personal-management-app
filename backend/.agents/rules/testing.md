# Testing Standards

## 1. Core Stack & Principles
- **Framework:** JUnit 5, Mockito, AssertJ, and Spring Test (`MockMvc`).
- **Assertions:** Prefer AssertJ (`assertThat(...)`) over JUnit's `assertEquals` for readability.
- **Package Mirroring:** Test classes must mirror the exact package path of the class under test under `src/test/java/com/example/personal_management_app/`.
- **Fast by Default:** Do **not** use `@SpringBootTest` for unit tests. Always use the lightest test slice possible.

## 2. Testing by Layer

### Service Layer (Unit Tests)
- Use `@ExtendWith(MockitoExtension.class)` with `@Mock` and `@InjectMocks` (no Spring context).
- Mock repositories, mappers, and external domain services.
- Cover both happy paths and domain exception scenarios (e.g., `assertThatThrownBy(...).isInstanceOf(NoteNotFoundException.class)`).

### Controller Layer (Web Slice Tests)
- Use `@WebMvcTest(controllers = <Entity>Controller.class)` with `MockMvc` and mocked services (`@MockitoBean` / `@MockBean`).
- Include the domain's `<Domain>SpecificExceptionHandler` (and root global handler if needed) when testing error responses.
- Verify:
  1. HTTP status codes.
  2. `RestApiResponse<T>` JSON structure (`data`, `message`, etc.).
  3. `@Valid` request validation failures (400 Bad Request).

### Repository Layer (Persistence Slice Tests)
- Use `@DataJpaTest` backed by **Testcontainers (PostgreSQL)** rather than H2, to match production SQL and Flyway/Liquibase migrations.
- **Only test custom `@Query` methods, complex filters, or composite key mappings.** Do not write tests for built-in Spring Data JPA methods like `save()` or `findById()`.

### Integration Tests
- Reserve `@SpringBootTest` for critical end-to-end flows (e.g., cross-domain interactions or security filter chains).

## 3. Structure & Naming
- Follow the **Arrange-Act-Assert** (or Given-When-Then) pattern with clear visual separation.
- **Class naming:** `<ClassName>Test` for unit/slice tests, `<Feature>IT` for full integration tests.
- **Method naming:** `<methodName>_should<ExpectedBehavior>_when<Condition>` (or use `@DisplayName`).

```java
@Test
void createUser_shouldReturnCreatedUser_whenRequestIsValid() {
    // Arrange
    CreateUserRequest request = new CreateUserRequest("john@example.com", "John");
    when(userRepository.existsByEmail(request.email())).thenReturn(false);
    // ...

    // Act
    UserDto result = userService.createUser(request);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.email()).isEqualTo("john@example.com");
}
```

## 4. Test Hygiene
- Keep tests independent—never rely on execution order or shared mutable state.
- Avoid hardcoding database auto-generated IDs in assertions when testing against a real database.
- Always run `./gradlew test` after writing or modifying tests to confirm they pass.