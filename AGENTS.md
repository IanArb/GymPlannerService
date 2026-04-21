# AGENTS.md

Guidance for AI agents contributing to GymPlannerService.

See [SKILL.md](.claude/skills/tests/SKILL.md) for detailed patterns on writing unit tests.

## Before making changes

- Read `CLAUDE.md` for commands, architecture, and conventions.
- Read the files you intend to modify before changing them.
- Understand the existing pattern in the feature you are touching before adding code.

## Commands to run after changes

Always run these before considering a task complete:

```bash
# Format code
./gradlew spotlessApply

# Check formatting (must pass)
./gradlew spotlessCheck

# Static analysis (must pass, zero issues allowed)
./gradlew detekt

# Run all tests (must pass)
./gradlew clean test
```

Never skip `spotlessCheck` or `detekt`. CI enforces both on every PR.

## Adding a new feature

Follow the vertical-slice pattern used by every existing feature:

```
<feature>/
  <Feature>Controller.kt
  <Feature>Service.kt          # interface only
  <Feature>ServiceImpl.kt      # implementation
  data/
    <Model>.kt                 # MongoDB @Document
    <Feature>Repository.kt     # CoroutineCrudRepository or ReactiveMongoRepository
```

Steps:
1. Define the `@Document` data class in `data/`.
2. Define the repository interface extending `CoroutineCrudRepository`.
3. Define the service interface and implement it in `ServiceImpl`.
4. Define the controller with `@RestController` and `suspend` endpoint functions.
5. Register the new route in `SecurityConfig.kt` if it should be public; otherwise it is JWT-protected by default.
6. Write a controller test (`@WebFluxTest` + `@MockitoBean`) and a service test (MockK).

## Writing tests

### Controller tests
- Use `@WebFluxTest(controllers = [YourController::class])`.
- Exclude security: `excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class]`.
- Annotate with `@ActiveProfiles("test")` and `@TestPropertySource("classpath:application-test.properties")`.
- Mock the service with `@MockitoBean`.
- Provide test data via a `DataProvider` object in `src/test/kotlin/.../mocks/`.

```kotlin
@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [MyController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
class MyControllerTests {
    @Autowired lateinit var webTestClient: WebTestClient
    @MockitoBean private lateinit var myService: MyService
    ...
}
```

### Service tests
- Do not use Spring context — instantiate the service directly.
- Mock dependencies with MockK (`mockk<MyRepository>()`).
- Use `coEvery` / `coVerify` for suspend functions.
- Use Turbine (`test { }`) for `Flow` assertions.
- Use Google Truth (`assertThat(...)`) for assertions.

```kotlin
class MyServiceTests {
    private val repository = mockk<MyRepository>()
    private val service = MyServiceImpl(repository)

    @Test
    fun `some behaviour`() = runTest {
        coEvery { repository.findAll() } returns flowOf(...)
        ...
    }
}
```

## Security rules

- All routes are JWT-protected by default via `SecurityConfig`.
- To make a route public, add its path to the `pathMatchers(...).permitAll()` block in `SecurityConfig.kt`.
- Never remove JWT protection from an existing authenticated route without explicit instruction.
- Do not log JWT secrets, passwords, or tokens.

## Code style rules

- All controller functions must be `suspend` or return `Flow<T>`.
- Use Kotlin coroutines — do not use blocking calls inside reactive/suspend contexts.
- Do not add comments unless the logic is non-obvious.
- Do not add error handling for scenarios that cannot happen.
- Do not create helpers or abstractions for one-time use.
- Keep `@Document` data classes in `data/` alongside their repository.

## What not to do

- Do not modify `application-test.properties` — tests depend on its exact values.
- Do not commit `firebase-service-account.json` or any file containing secrets.
- Do not add `cors.allowed-origins` entries for production domains without explicit instruction.
- Do not amend existing commits — always create new ones.
- Do not push to remote unless explicitly asked.