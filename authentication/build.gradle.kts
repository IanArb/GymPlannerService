// Authentication domain: the User model + repository, sign-up/login DTOs, the
// AuthenticationController/Service, and the adapter that satisfies the security
// layer's user-lookup port. Depends only on :core-utils and :security — never on
// feature modules (cross-feature calls are inverted behind ports it owns).
plugins {
    id("gymplanner.spring-conventions")
    // Publishes UserProfileDataProvider (UserProfile fixtures) consumed by
    // :booking and the user-profile tests still in :app.
    `java-test-fixtures`
}

dependencies {
    implementation(project(":core-utils"))
    implementation(project(":security"))

    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.data.mongodb.reactive)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi.webflux.ui)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.spring.boot.starter.data.mongodb.test)
    testImplementation(libs.flapdoodle.mongo)
}
