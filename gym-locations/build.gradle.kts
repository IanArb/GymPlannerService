// Gym-locations feature: self-contained vertical slice (controller + service +
// MongoDB document/repository) with no dependency on other features.
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":domain"))

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
    // GymLocationsProvider lives in :domain test fixtures.
    testImplementation(testFixtures(project(":domain")))
}
