// Booking feature — the most connected: depends on :authentication, :availability,
// :trainers, :push-notifications and :core-utils. Its dependency on the user-profile
// feature is inverted behind the UserProfileGateway port (implemented in :app), so
// :booking does not depend on user-profile (which still lives in :app).
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":core-utils"))
    implementation(project(":authentication"))
    implementation(project(":availability"))
    implementation(project(":trainers"))
    implementation(project(":push-notifications"))

    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.data.mongodb.reactive)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi.webflux.ui)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.spring.boot.starter.data.mongodb.test)
    testImplementation(libs.flapdoodle.mongo)
    // Only for the @WebFluxTest's ReactiveWebSecurityAutoConfiguration exclusion.
    testImplementation(libs.spring.boot.starter.security)
    // AvailabilityDataProvider fixture (BookingServiceTests). The user-profile
    // dependency is behind the UserProfileGateway port, so no auth fixture needed.
    testImplementation(testFixtures(project(":availability")))
}
