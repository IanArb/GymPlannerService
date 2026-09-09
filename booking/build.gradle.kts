// Booking feature — the most connected: depends on :authentication, :availability,
// :trainers, :push-notifications and :core-utils. Its dependency on the user-profile
// feature is inverted behind the UserProfileGateway port (implemented in :app), so
// :booking does not depend on user-profile (which still lives in :app).
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-utils"))
    implementation(project(":authentication"))
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
    // DataProviders (Booking/Availability/User/...) live in :domain test fixtures.
    testImplementation(testFixtures(project(":domain")))
}
