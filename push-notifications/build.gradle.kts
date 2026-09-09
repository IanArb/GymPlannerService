// Push-notifications feature. Currently FCM-only (package `fcm`); the module is
// named provider-agnostically so other providers (e.g. APNS for iOS) can be added
// as sibling packages behind a shared abstraction later.
//
// Depends on :authentication because FcmTokenService stores the push token on the
// User via UserRepository (mongodb-reactive is needed to resolve the repository's
// CoroutineCrudRepository supertype).
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":authentication"))

    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.data.mongodb.reactive)
    implementation(libs.firebase.admin)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.spring.boot.starter.data.mongodb.test)
    testImplementation(libs.flapdoodle.mongo)
    // Only for the @WebFluxTest's ReactiveWebSecurityAutoConfiguration exclusion.
    testImplementation(libs.spring.boot.starter.security)
}
