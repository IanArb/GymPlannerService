---
name: design-patterns
description: Follow these patterns and conventions when adding new features or extending existing ones in GymPlannerService.
allowed-tools: Read Glob Grep Write Edit
argument-hint: "[feature name]"
---
# SKILL.md — Design Patterns

Patterns and conventions for adding new features or extending existing ones in GymPlannerService.

---

## Vertical-slice module structure

Every feature is a self-contained vertical slice. Never put shared business logic in a common package.

```
<feature>/
  <Feature>Controller.kt          # HTTP layer only
  data/
    <Model>.kt                    # @Document data class
    <Feature>Repository.kt        # CoroutineCrudRepository
    <Feature>Service.kt           # Interface + Impl in the same file
  exception/
    Exceptions.kt                 # Domain exceptions, one file per feature
```

New feature checklist:
- [ ] Controller maps exceptions → HTTP status codes (no business logic)
- [ ] Service interface + `@Service` impl in the same file
- [ ] Repository extends `CoroutineCrudRepository<T, String>`
- [ ] All domain exceptions are `RuntimeException` subclasses in `exception/Exceptions.kt`
- [ ] Route registered in `SecurityConfig` if it requires JWT auth (default) or is public

---

## Controller pattern

Controllers are thin. They delegate everything to the service and convert domain exceptions into `ResponseStatusException`.

```kotlin
@RestController
@RequestMapping("/api/v1/<feature>")
@Tag(name = "<Feature>", description = "Endpoints for <feature>")
class FeatureController(
    private val service: FeatureService,
) {
    @GetMapping
    suspend fun fetchAll(): Flow<Feature> =
        try {
            service.fetchAll()
        } catch (ex: FeatureNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Not found", ex)
        }

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable id: String): Feature =
        service.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Not found")

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody body: Feature): Feature = service.create(body)

    @PutMapping
    suspend fun update(@RequestBody body: Feature) = service.update(body)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun delete(@PathVariable id: String) = service.deleteById(id)
}
```

**Rules:**
- Return `Flow<T>` for list endpoints, `T?` (with null → 404) for single-item endpoints.
- Never catch generic `Exception` — only the specific domain exception you expect.
- `@ResponseStatus` belongs on the method, not on the exception class.

---

## Service pattern

Interface and implementation live in the same file. The implementation is annotated `@Service`.

```kotlin
interface FeatureService {
    fun fetchAll(): Flow<Feature>
    suspend fun findById(id: String): Feature?
    suspend fun create(feature: Feature): Feature
    suspend fun update(feature: Feature)
    suspend fun deleteById(id: String)
}

@Service
class FeatureServiceImpl(
    private val repository: FeatureRepository,
) : FeatureService {

    override fun fetchAll(): Flow<Feature> = repository.findAll()

    override suspend fun findById(id: String): Feature? = repository.findById(id)

    override suspend fun create(feature: Feature): Feature = repository.save(feature)

    override suspend fun update(feature: Feature) {
        repository.save(feature).takeIf { repository.existsById(feature.id ?: "") }
    }

    override suspend fun deleteById(id: String) = repository.deleteById(id)
}
```

**Rules:**
- `fetchAll` / `findAll` return `Flow<T>` (non-suspend) — reactive stream, not a one-shot result.
- Single-item lookups are `suspend` and return `T?` — null means not found; throw only when the caller needs to distinguish missing from other errors.
- Cross-feature validation belongs in the service (e.g. validating a trainer exists before saving a booking), not in the controller or repository.
- When a service depends on another feature's repository, inject that repository directly — do not call another feature's service.

---

## Repository pattern

```kotlin
@Repository
interface FeatureRepository : CoroutineCrudRepository<Feature, String> {
    fun findAllByGymLocation(gymLocation: GymLocation): Flow<Feature>
    suspend fun findByUserId(userId: String): Feature?
}
```

**Rules:**
- Use Spring Data derived query method names (`findAllBy…`, `findBy…`).
- Custom query methods that return multiple results use `Flow<T>` (non-suspend).
- Custom query methods that return a single result are `suspend` and return `T?`.
- Never write `@Query` annotations unless the derived name becomes unreadable.

---

## Model pattern

```kotlin
@Document
data class Feature(
    @BsonId val id: String? = null,
    @field:NotNull(message = "Name is mandatory") val name: String,
    val gymLocation: GymLocation,
    val status: FeatureStatus = FeatureStatus.ACTIVE,
)

enum class FeatureStatus { ACTIVE, INACTIVE }
```

**Rules:**
- `id` is always `String?` with default `null` — MongoDB generates it on insert.
- Use `@BsonId`, not `@Id`, for the primary key.
- Bean validation annotations go on the field: `@field:NotNull`, `@field:FutureDate`.
- Enums live in the same file as the model they belong to unless shared across features.
- Shared enums (e.g. `GymLocation`) live in `common/`.

---

## Exception pattern

All domain exceptions are sealed under a single file per feature.

```kotlin
// exception/Exceptions.kt
class FeatureNotFoundException : RuntimeException("Feature not found")
class FeatureAlreadyExistsException : RuntimeException("Feature already exists")
```

**Rules:**
- Extend `RuntimeException`, not `Exception`.
- Set a human-readable default message in the constructor.
- The controller catches these and maps to `ResponseStatusException` — exceptions must not carry HTTP knowledge.

---

## Cross-feature dependency pattern

When a service needs to validate entities from another feature (e.g. booking validates that a trainer exists), inject the *repository* of that feature — not its service.

```kotlin
@Service
class BookingServiceImpl(
    private val bookingsRepository: BookingRepository,
    private val personalTrainersRepository: PersonalTrainerRepository, // cross-feature repo
) : BookingService {

    private suspend fun validatePersonalTrainer(id: String) {
        personalTrainersRepository.findById(id) ?: throw PersonalTrainerNotFoundException()
    }
}
```

---

## Custom validation annotation pattern

Use when a constraint is reusable across multiple models (e.g. `@FutureDate`).

```kotlin
@MustBeDocumented
@Constraint(validatedBy = [FeatureValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureConstraint(
    val message: String = "Validation failed",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class FeatureValidator : ConstraintValidator<FeatureConstraint, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean =
        value != null && value.isNotBlank()
}
```

Place annotation + validator in `src/main/kotlin/.../validation/`.

---

## Enum-based routing pattern

When a service branches on an enum, use a `when` expression that covers all cases — never use `else`. This ensures the compiler enforces exhaustiveness when new values are added.

```kotlin
override fun findByLocation(location: GymLocation): Flow<Feature> =
    when (location) {
        GymLocation.CLONTARF,
        GymLocation.ASTONQUAY,
        GymLocation.LEOPARDSTOWN,
        GymLocation.DUNLOAGHAIRE,
        GymLocation.WESTMANSTOWN,
        GymLocation.SANDYMOUNT, -> repository.findAllByGymLocation(location)
    }
```

---

## Checklist before opening a PR

- [ ] Module follows the vertical-slice directory layout
- [ ] Controller only contains HTTP mapping and exception → status conversion
- [ ] Service interface and impl are in the same file
- [ ] Repository uses derived query names, returns `Flow<T>` for collections
- [ ] Model uses `@BsonId`, `id: String? = null`, field-level validation annotations
- [ ] Domain exceptions extend `RuntimeException` with a default message
- [ ] Cross-feature access goes through the other feature's repository, not its service
- [ ] `when` on enums has no `else` branch
- [ ] `./gradlew spotlessCheck` passes
- [ ] `./gradlew detekt` passes