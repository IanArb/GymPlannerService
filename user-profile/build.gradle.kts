// User-profile feature. Depends on :authentication (the UserProfile model + the
// UserProfileRegistrar port) and :booking (the UserProfileGateway port); it
// implements both ports here, so those modules never depend back on this one.
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":core-utils"))
    implementation(project(":authentication"))
    implementation(project(":booking"))

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
    // UserProfile tests use UserProfileDataProvider (fixture in :authentication).
    testImplementation(testFixtures(project(":authentication")))
}
