// Personal trainers feature. Depends only on :domain (which re-exports :core-utils,
// where GymLocation lives). The booking/availability gateway *ports* it used to
// implement are now wired by adapters in :app, so this feature no longer depends on
// :booking or :availability. Publishes a test-fixtures artifact
// (PersonalTrainerDataProvider) consumed by :checkin and :app tests.
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
    // DataProviders (PersonalTrainerDataProvider, ...) live in :domain test fixtures.
    testImplementation(testFixtures(project(":domain")))
}
