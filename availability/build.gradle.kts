// Trainer availability feature. Depends on :trainers (PersonalTrainerRepository)
// and :core-utils (relocated PersonalTrainerNotFoundException). Publishes
// AvailabilityDataProvider as a test fixture consumed by :booking (and :app while
// booking still lives there).
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-utils"))
    implementation(project(":trainers"))
    implementation(project(":booking"))

    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.data.mongodb.reactive)
    implementation(libs.springdoc.openapi.webflux.ui)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.spring.boot.starter.data.mongodb.test)
    testImplementation(libs.flapdoodle.mongo)
    // Only for the @WebFluxTest's ReactiveWebSecurityAutoConfiguration exclusion.
    testImplementation(libs.spring.boot.starter.security)
    // DataProviders (Availability/PersonalTrainer/...) live in :domain test fixtures.
    testImplementation(testFixtures(project(":domain")))
}
