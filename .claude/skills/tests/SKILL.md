---
name: tests
description: Follow these testing patterns on how to create integration and unit tests
allowed-tools: Read Glob Grep Write Edit
argument-hint: "[feature name]"
---
# SKILL.md — Writing Unit Tests

Patterns and conventions for writing unit tests in GymPlannerService.

---

## Two kinds of tests

| Type | Scope | Framework |
|---|---|---|
| Controller test | HTTP layer only — no business logic | `@WebFluxTest` + Mockito |
| Service test | Business logic only — no Spring context | MockK, instantiated directly |

Never mix these. A controller test never tests service logic. A service test never starts a Spring context.

---

## Controller tests

### Required annotations

```kotlin
@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [YourController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class YourControllerTests {
    @Autowired private lateinit var webTestClient: WebTestClient
    @MockitoBean private lateinit var yourService: YourService
}
```

- `excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class]` — always required; removes JWT filter from tests.
- `@AutoConfigureDataMongo` — always required; provides the embedded Flapdoodle MongoDB context.
- `@ActiveProfiles("test")` + `@TestPropertySource(...)` — always required; loads `application-test.properties`.

### Mocking with Mockito

Use backtick test names. Use `when(...).thenReturn(...)` for stubbing.

```kotlin
@Test
fun `should return all items`() = runTest {
    // Given
    val items = listOf(DataProvider.create(), DataProvider.create(id = "2"))
    `when`(yourService.findAll()).thenReturn(flowOf(*items.toTypedArray()))

    // When & Then
    webTestClient
        .get()
        .uri("/api/v1/your-route")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .jsonPath("$[0].fieldName").isEqualTo("expectedValue")
}
```

### HTTP status assertions

| Scenario | Assertion |
|---|---|
| Successful GET / PUT | `.expectStatus().isOk` |
| Successful POST | `.expectStatus().isCreated` |
| Successful DELETE | `.expectStatus().isNoContent` |
| Not found | `.expectStatus().isNotFound` |
| Validation failure | `.expectStatus().isBadRequest` |

### Testing 404 responses

Return `null` from the mock — the controller maps `null` to 404.

```kotlin
@Test
fun `should return 404 when not found`() = runTest {
    `when`(yourService.findById("999")).thenReturn(null)

    webTestClient
        .get()
        .uri("/api/v1/your-route/999")
        .exchange()
        .expectStatus().isNotFound
}
```

### Testing validation errors (400)

Pass a raw JSON string with an invalid enum or missing required field. Assert `$.error` is not empty.

```kotlin
@Test
fun `should return 400 when body is invalid`() = runTest {
    val invalidBody = """
        {
            "fieldName": "INVALID_ENUM_VALUE"
        }
    """.trimIndent()

    webTestClient
        .post()
        .uri("/api/v1/your-route")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidBody)
        .exchange()
        .expectStatus().isBadRequest
        .expectBody()
        .jsonPath("$.error").isNotEmpty
}
```

### Testing PUT / POST with a body

```kotlin
webTestClient
    .put()
    .uri("/api/v1/your-route/1")
    .contentType(MediaType.APPLICATION_JSON)
    .bodyValue(yourObject)
    .exchange()
    .expectStatus().isOk
    .expectBody(YourModel::class.java)
    .isEqualTo(yourObject)
```

---

## Service tests

No Spring context. Instantiate the service directly with mocked dependencies.

### Structure

```kotlin
class YourServiceTests {
    private val repository: YourRepository = mockk()
    private val service: YourService = YourServiceImpl(repository)

    @Test
    fun `should do something`() = runTest {
        // Given
        // When
        // Then
    }
}
```

### Mocking suspend functions

Use `coEvery` and `coVerify` for any `suspend` function.

```kotlin
coEvery { repository.findById("1") } returns someObject
coVerify { repository.findById("1") }
```

### Asserting on Flow results — use Turbine

```kotlin
@Test
fun `should return all items`() = runTest {
    // Given
    val items = listOf(DataProvider.create(), DataProvider.create(id = "2"))
    coEvery { repository.findAll() } returns flowOf(*items.toTypedArray())

    // When & Then
    service.findAll().test {
        assertThat(awaitItem()).isEqualTo(items.first())
        assertThat(awaitItem()).isEqualTo(items.last())
        awaitComplete()
    }

    coVerify { repository.findAll() }
}
```

### Asserting on suspend functions returning a single value

```kotlin
@Test
fun `should return item by id`() = runTest {
    // Given
    val item = DataProvider.create()
    coEvery { repository.findById("1") } returns item

    // When
    val result = service.findById("1")

    // Then
    assertThat(result).isEqualTo(item)
    coVerify { repository.findById("1") }
}
```

### Asserting null

```kotlin
coEvery { repository.findById("999") } returns null

val result = service.findById("999")

assertThat(result).isNull()
```

### Asserting exceptions

Use `assertThrows` from JUnit 5. Use `assertWithMessage` for message content.

```kotlin
@Test
fun `should throw when not found`() = runTest {
    coEvery { repository.findById("1") } returns null

    val exception = assertThrows<YourNotFoundException> {
        service.findById("1")
    }

    assertThat(exception).isInstanceOf(YourNotFoundException::class.java)
    assertWithMessage("Expected exception message")
        .that(exception)
        .hasMessageThat()
        .contains("expected message text")
}
```

### Verifying save / delete calls

```kotlin
@Test
fun `should save item`() = runTest {
    val item = DataProvider.create()
    coEvery { repository.save(item) } returns item

    service.save(item)

    coVerify { repository.save(item) }
}

@Test
fun `should delete item by id`() = runTest {
    coEvery { repository.deleteById("1") } returns Unit

    service.deleteById("1")

    coVerify { repository.deleteById("1") }
}
```

---

## DataProvider objects

Test data lives in `src/test/kotlin/.../mocks/` as `object` types with factory functions. All parameters have defaults so tests only override what they care about.

```kotlin
object YourDataProvider {
    fun create(
        id: String? = "1",
        name: String = "Default Name",
        status: YourStatus = YourStatus.ACTIVE,
    ): YourModel = YourModel(
        id = id,
        name = name,
        status = status,
    )
}
```

Use named parameters when overriding in tests:

```kotlin
DataProvider.create(id = "2", status = YourStatus.INACTIVE)
```

---

## Assertions library

Use **Google Truth** for all assertions. Never use JUnit's `assertEquals` or Kotlin's `assertEquals`.

```kotlin
// Correct
assertThat(result).isEqualTo(expected)
assertThat(result).isNull()
assertThat(result).isNotNull()
assertThat(result).isInstanceOf(SomeException::class.java)

// Exception messages
assertWithMessage("context").that(exception).hasMessageThat().contains("text")
```

---

## Test naming

Use backtick strings. Format: `should <behaviour> when <condition>` or `<method> should <outcome>`.

```kotlin
fun `should return 404 when machine not found by id`()
fun `fetchBookingById should return booking when found`()
fun `should throw exception when personal trainer already booked`()
```

---

## Checklist before submitting tests

- [ ] Controller test has all four required annotations
- [ ] Security is excluded from `@WebFluxTest`
- [ ] Service test has no Spring annotations
- [ ] `Flow` assertions use Turbine with `awaitComplete()`
- [ ] Exception assertions use `assertThrows` + `assertWithMessage`
- [ ] All assertions use Google Truth
- [ ] Test data uses a `DataProvider` object
- [ ] `./gradlew clean test` passes