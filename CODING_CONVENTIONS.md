# Coding Conventions for Gepardec GamerTrack

This document defines the coding standards, architectural guidelines, and design patterns established across the **GamerTrack** codebase. Following these conventions maximizes team efficiency, reduces cognitive overhead, ensures high maintainability, and prevents defect propagation across all modules.

---

## 1. Architectural Structure & Module Responsibilities

The project follows a **Clean / Ports-and-Adapters (Hexagonal)** multi-module Maven architecture:

```
gepardec-gamertrack
├── gamertrack-domain          # Core domain models, interfaces, and business services
├── gamertrack-db              # Persistence adapter (JPA entities, repositories, mappers)
├── gamertrack-application     # REST API layer (JAX-RS resources, DTOs, commands, security)
├── gamertrack-war             # Application packaging, assembly, and deployment config
└── gamertrack-IntegrationTest # End-to-end integration tests (REST-assured & QuarkusTest)
```

### Module Boundaries
- **`gamertrack-domain`**:
  - Independent of framework-specific persistence (no JPA / Hibernate imports) and web layers.
  - Contains core domain models (`com.gepardec.model`), repository contracts (`com.gepardec.core.repository`), service contracts (`com.gepardec.core.services`), and service implementations (`com.gepardec.impl.service`).
  - Contains core security utilities (`com.gepardec.security`).
- **`gamertrack-db`**:
  - Implements the persistence output ports defined in `gamertrack-domain`.
  - Contains JPA entities (`com.gepardec.adapter.output.persistence.entity`), repository implementations (`com.gepardec.adapter.output.persistence.repository`), and entity mappers (`com.gepardec.adapter.output.persistence.repository.mapper`).
- **`gamertrack-application`**:
  - Exposes HTTP REST interfaces (`com.gepardec.rest.api`) and implementations (`com.gepardec.rest.impl`).
  - Contains immutable REST DTOs (`com.gepardec.rest.model.dto`), Commands (`com.gepardec.rest.model.command`), request mappers (`com.gepardec.rest.model.mapper`), and security/interceptor infrastructure (`com.gepardec.rest.config`).
- **`gamertrack-IntegrationTest`**:
  - Dedicated module for black-box integration tests verifying end-to-end functionality across HTTP endpoints.

---

## 2. Naming Conventions

Consistency across naming eliminates ambiguity and facilitates rapid comprehension:

| Artifact Type | Convention / Pattern | Example |
| :--- | :--- | :--- |
| **REST Resource Interface** | `<Entity>Resource` | `GameResource`, `UserResource` |
| **REST Resource Implementation** | `<Entity>ResourceImpl` | `GameResourceImpl`, `UserResourceImpl` |
| **Domain Service Interface** | `<Entity>Service` | `GameService`, `UserService` |
| **Domain Service Implementation** | `<Entity>ServiceImpl` | `GameServiceImpl`, `UserServiceImpl` |
| **Repository Interface** | `<Entity>Repository` | `GameRepository`, `UserRepository` |
| **Repository Implementation** | `<Entity>RepositoryImpl` | `GameRepositoryImpl`, `UserRepositoryImpl` |
| **JPA Entity** | `<Entity>Entity` | `GameEntity`, `UserEntity` |
| **REST DTO (Response)** | `<Entity>RestDto` (Java `record`) | `GameRestDto`, `UserRestDto` |
| **Command (Request Body)** | `Create<Entity>Command`, `Update<Entity>Command` (`record`) | `CreateGameCommand`, `UpdateGameCommand` |
| **REST Mapper** | `<Entity>RestMapper` | `GameRestMapper`, `UserRestMapper` |
| **Entity Mapper** | `<Entity>Mapper` | `GameMapper`, `UserMapper` |
| **Unit Test Class** | `<ClassUnderTest>Test` | `GameServiceImplTest`, `GameRepositoryTest` |
| **Integration Test Class** | `<ResourceUnderTest>IT` | `GameResourceImplIT`, `AuthResourceImplIT` |
| **Test Fixtures** | `TestFixtures`, `RestTestFixtures` | `TestFixtures` |
| **Test Methods** | `ensure<Action><Condition><ExpectedResult>` | `ensureSavingValidGameWorksAndReturnsValidGame()` |

---

## 3. REST API Design & Contracts

### Interface-First REST Endpoints
- Define API contracts in interfaces under `com.gepardec.rest.api` and implement them under `com.gepardec.rest.impl`.
- Annotate the interface with OpenAPI documentation annotations (`@Operation`, `@ApiResponses`, `@ApiResponse`, `@RequestBody`, `@Schema`).
- Standardize media types at the interface level:
  ```java
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("games")
  public interface GameResource { ... }
  ```
- Use plural nouns for endpoints (`/games`, `/users`, `/matches`, `/scores`).

### Identifier & Token Strategy
- **External Exposure**: Never expose internal database surrogate IDs (`Long id`) in public REST endpoints.
- **Natural Keys / Tokens**: Use public UUID-based tokens (`String token`) for external identification in paths (e.g., `GET /games/{token}`, `PUT /games/{token}`, `DELETE /games/{token}`).

### HTTP Methods & Status Codes
- `GET`: Returns `200 OK` with entity or list; returns `404 Not Found` if a specific resource is missing. Returns `200 OK` with an empty collection `[]` if no elements exist.
- `POST`: Returns `201 Created` with created DTO on success; `400 Bad Request` if creation failed or payload is invalid.
- `PUT`: Returns `200 OK` with updated DTO on success; `404 Not Found` if entity to update does not exist.
- `DELETE`: Returns `200 OK` with deleted DTO or `204 No Content`; `404 Not Found` if entity does not exist.

---

## 4. CDI & Transaction Management

- **Scope Annotations**:
  - Use `@ApplicationScoped` for stateful/stateless shared components: Services (`*ServiceImpl`), Repositories (`*RepositoryImpl`), and Mappers (`*Mapper`, `*RestMapper`).
  - Use `@RequestScoped` for JAX-RS Resource implementations (`*ResourceImpl`).
- **Dependency Injection**:
  - Use Jakarta `@Inject` for injecting dependencies.
- **Transactions**:
  - Annotate service implementations and repository implementations with `@Transactional` from `jakarta.transaction.Transactional`.

---

## 5. Domain Models, Entities & Mappers

### Domain Models (`com.gepardec.model`)
- Plain Java objects (POJOs) with no JPA or ORM annotations.
- Provide explicit constructors (no-args and full-args), getters, setters, `equals()`, `hashCode()`, and `toString()`.

### Database Entities (`com.gepardec.adapter.output.persistence.entity`)
- Extend `AbstractEntity` to inherit common auditing and identity fields (`id`, `version`, `createdOn`, `updatedOn`).
- Use `@Entity` and `@Table` with explicit indexes and unique constraints (e.g., `@Table(name = "games", indexes = @Index(name = "ux_games_token", columnList = "token", unique = true))`).
- Apply Bean Validation constraints (e.g., `@NotBlank`, `@NotNull`, `@Valid`) on entity attributes.

### DTOs & Commands (`com.gepardec.rest.model`)
- Implemented as Java **`record`** types for immutability and concise syntax.
- DTOs provide convenience constructor converting from domain model:
  ```java
  public record GameRestDto(String token, String name, String rules) {
    public GameRestDto(Game game) {
      this(game.getToken(), game.getName(), game.getRules());
    }
  }
  ```
- Commands include validation constraints:
  ```java
  public record CreateGameCommand(@NotBlank String name, String rules) {}
  ```

### Mappers
- Keep mapping logic separated into dedicated `@ApplicationScoped` mapper classes (`*RestMapper` and `*Mapper`).
- Implement methods for:
  - `commandToModel(...)`
  - `entityToModel(...)`
  - `modelToEntity(...)`
  - `modelToExistingEntity(...)`

---

## 6. Security & Logging Guidelines

### Strict Secret Protection
- **Rule**: NEVER log passwords, plain authentication tokens, Authorization headers, or JWT strings.
- Always sanitize before logging:
  - Use `TokenLogUtil.categorize(exception)` to log error categories without exposing tokens.
  - Use `TokenLogUtil.fingerprint(token)` when a non-reversible correlation ID is required.
- Mark authenticated endpoints with `@Secure`, which is intercepted and validated by `AuthFilter`.

### Logging Standards
- Use SLF4J: `private final Logger logger = LoggerFactory.getLogger(MyClass.class);`
- Use formatted strings or parameterized placeholders:
  ```java
  logger.info("Saving game: %s".formatted(game));
  logger.info("Found game: {}", game);
  ```

---

## 7. Error Handling & Functional Patterns

- **`Optional<T>` over `null`**:
  - Repository and Service methods returning single values must return `Optional<T>`.
  - Handle missing data functionally via `.map(...)`, `.orElseGet(...)`, `.ifPresentOrElse(...)`.
- **Collections**:
  - Never return `null` for list results; return empty lists (e.g., `List.of()` or empty `ArrayList`).
  - Use Java Stream API (`stream().map(...).toList()`, `filter(...)`, `collect(...)`).

---

## 8. Code Formatting & Modern Java Idioms

- **Java Version**: Target **Java 21**.
- **Indentation**: Use **2 spaces** indentation consistently for Java source files.
- **Local Variable Type Inference**: Use `var` for local variables in tests or where the RHS type is immediately evident:
  ```java
  var savedGame = gameService.saveGame(game);
  ```
- **String Formatting**: Prefer `String.formatted(...)` or string interpolation over manual concatenation.

---

## 9. Testing Standards & Conventions

### Unit Tests (`*Test.java`)
- Use JUnit 5 and Mockito (`@ExtendWith(MockitoExtension.class)`).
- Mock collaborators using `@Mock` and inject into system-under-test with `@InjectMocks`.
- Focus unit tests on domain service logic, validation rules, and mapping edge cases.

### Integration Tests (`*IT.java`)
- Annotate with `@QuarkusTest`.
- Use REST Assured (`given()`, `when()`, `then()`) for black-box endpoint validation.
- Standard test lifecycle:
  - `@BeforeAll`: Configure REST Assured logging (`enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL)`).
  - `@BeforeEach`: Perform authentication setup and obtain test JWT bearer tokens.
  - `@AfterEach`: Clean up generated resources using their tokens.
  - `@AfterAll`: Call `RestAssured.reset()`.

### Test Fixtures
- Centralize test entity and model generation in `TestFixtures` (unit tests) and `RestTestFixtures` (integration tests).
- Provide overloaded methods for creating single instances and collections:
  - `TestFixtures.game()`, `TestFixtures.game(Long id)`, `TestFixtures.games(int count)`.

### Test Method Naming Structure
- All test methods must follow the naming pattern:
  `ensure<Action><Condition><ExpectedResult>()`
  - Examples:
    - `ensureSavingValidGameWorksAndReturnsValidGame()`
    - `ensureSavingAlreadyExistingGameFailsAndReturnsOptionalEmpty()`
    - `ensureGetGameWithExistingGameReturnsGame()`
    - `ensureDeleteNotExistingGameReturns404NotFound()`
