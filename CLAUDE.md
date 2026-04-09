# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the application (requires env vars set)
./gradlew bootRun

# Run all tests
./gradlew clean test

# Build
./gradlew build

# Run a single test class
./gradlew test --tests "com.ianarbuckle.gymplannerservice.booking.BookingsControllerTests"

# Run a single test method
./gradlew test --tests "com.ianarbuckle.gymplannerservice.booking.BookingsControllerTests.fetchAllBookings should return all bookings"

# Lint (code formatting check)
./gradlew spotlessCheck

# Apply formatting
./gradlew spotlessApply

# Static analysis
./gradlew detekt
```

## Environment Variables

The app requires these env vars at runtime (not needed for tests):

| Variable | Purpose |
|---|---|
| `MONGO_URI` | MongoDB connection string |
| `MONGO_DATABASE_NAME` | MongoDB database name |
| `ENVIRONMENT` | Active Spring profile (`dev`, `staging`, `production`) |
| `JWT_EXPIRY` | JWT expiration in milliseconds |
| `JWT_SECRET_KEY` | JWT signing secret |

Tests use `application-test.properties` with an embedded Flapdoodle MongoDB — no external DB needed.

## Architecture

**Spring Boot WebFlux** reactive service (Kotlin coroutines + Flow) backed by **MongoDB** (reactive). All controllers are `suspend` functions or return `Flow<T>`.

### Module structure

Each feature follows the same vertical-slice pattern:

```
<feature>/
  <Feature>Controller.kt       # REST endpoints, maps exceptions → HTTP status
  <Feature>Service.kt          # Business logic interface
  <Feature>ServiceImpl.kt      # Implementation (or inline)
  data/
    <Model>.kt                 # MongoDB @Document data classes
    <Feature>Repository.kt     # ReactiveMongoRepository / CoroutineCrudRepository
```

### Features / API routes

| Feature | Base route |
|---|---|
| Authentication | `/api/v1/auth/**` (public) |
| Booking | `/api/v1/booking` |
| Availability | `/api/v1/availability` |
| Fitness Classes | `/api/v1/classes` |
| Personal Trainers | `/api/v1/trainers` |
| Gym Locations | `/api/v1/locations` |
| Fault Reporting | `/api/v1/fault-report` |
| Messages | `/api/v1/messages` |
| User Profile | `/api/v1/user-profile` |
| Facility Status | `/api/v1/facilities` |

### Security

JWT-based auth via `SecurityConfig`. All routes require a Bearer token **except** `/api/v1/auth/**` and Swagger UI paths. The filter chain uses a custom `JWTAuthenticationManager` and `JwtServerAuthenticationConverter`.

### Validation

- Custom `@FutureDate` annotation (`FutureDateValidator.kt`) validates booking dates are in the future.
- `GlobalExceptionHandler` (`@RestControllerAdvice`) converts `MethodArgumentNotValidException` to 400 responses with field-level error maps.

### Testing conventions

- Controller tests use `@WebFluxTest` + `@MockitoBean` (Mockito) with security excluded via `excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class]`.
- Service tests use **MockK**.
- Test data is provided via `DataProvider` objects in `src/test/kotlin/.../mocks/`.
- All tests are annotated `@ActiveProfiles("test")` and `@TestPropertySource("classpath:application-test.properties")`.

### Code style

- Formatting: **ktfmt** (Kotlin language style) enforced by Spotless.
- Static analysis: **Detekt** with `detekt.yml` config, zero-issue tolerance (`maxIssues: 0`).
- CI runs `spotlessCheck` and `detekt` on every PR — run both locally before pushing.

### Firebase

FCM push notifications use `firebase-service-account.json` (in `src/main/resources/`). The Firebase Admin SDK is initialized in the app configuration.

### OpenAPI docs

Available when running locally:
- Swagger UI: `http://localhost:8080/webjars/swagger-ui/index.html`
- JSON spec: `http://localhost:8080/v3/api-docs`