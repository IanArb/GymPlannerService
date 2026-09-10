// Check-in feature. The PersonalTrainer entity and TrainerAvailabilityStatus live in
// :domain; the trainer lookup/update it needs is inverted behind the
// PersonalTrainerGateway port (implemented by an adapter in :app), so this feature
// depends on no other feature module. PersonalTrainerDataProvider comes from
// :domain's test fixtures.
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
    // DataProviders (CheckIn/PersonalTrainer/...) live in :domain test fixtures.
    testImplementation(testFixtures(project(":domain")))
}
