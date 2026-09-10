// Trainer availability feature. Depends on :domain and :core-utils (relocated
// PersonalTrainerNotFoundException). The trainer lookup it needs is inverted behind
// the PersonalTrainerGateway port (implemented by an adapter in :app), and its own
// AvailabilityGateway port is likewise implemented in :app, so this feature depends
// on neither :trainers nor :booking.
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-utils"))

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
