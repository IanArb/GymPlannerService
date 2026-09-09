// Facility-status feature. Depends on :core-utils for the GymLocation enum.
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-utils"))

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
    // FacilityStatusDataProvider lives in :domain test fixtures.
    testImplementation(testFixtures(project(":domain")))
}
