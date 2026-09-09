// Shared domain layer: the persistence entities, value types and enums used across
// features. A base module every feature depends on. Its test fixtures hold ALL the
// *DataProvider builders (they only construct domain objects), so any module's tests
// get them via a single `testFixtures(project(":domain"))` dependency.
plugins {
    id("gymplanner.spring-conventions")
    `java-test-fixtures`
}

dependencies {
    // GymLocation enum and @FutureDate appear in entity public API.
    api(project(":core-utils"))

    // Entities carry @Document / @Schema / Jakarta-validation annotations, which are
    // part of their compiled surface — expose via `api` so consumers and the test
    // fixtures can resolve the entity classes.
    api(libs.spring.boot.starter.data.mongodb.reactive)
    api(libs.spring.boot.starter.validation)
    api(libs.springdoc.openapi.webflux.ui)

    // The Spring Boot BOM (added by the convention to implementation/testImplementation)
    // doesn't reach the testFixtures classpath, so the version-less starters that arrive
    // via :domain's api deps can't resolve there without it.
    testFixturesImplementation(platform("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}"))
    // Some DataProviders build Flow-based fixtures.
    testFixturesImplementation(libs.kotlinx.coroutines.reactor)
}
