// Check-in feature. Depends on :trainers (PersonalTrainerRepository,
// TrainerAvailabilityStatus) and consumes its PersonalTrainerDataProvider test
// fixture. (:core-utils comes transitively via :trainers' api dependency.)
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":trainers"))

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
    // CheckInServiceTests uses PersonalTrainerDataProvider (test fixture in :trainers).
    testImplementation(testFixtures(project(":trainers")))
}
