// Push-notifications feature. Currently FCM-only (package `fcm`); the module is
// named provider-agnostically so other providers (e.g. APNS for iOS) can be added
// as sibling packages behind a shared abstraction later.
//
// FcmTokenService stores the push token on the User, but that lookup/update is
// inverted behind the UserGateway port (implemented by an adapter in :app), so this
// feature depends on no other feature module. The User entity itself lives in
// :domain, which also supplies the mongodb-reactive API transitively.
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.firebase.admin)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.spring.boot.starter.data.mongodb.test)
    testImplementation(libs.flapdoodle.mongo)
    // Only for the @WebFluxTest's ReactiveWebSecurityAutoConfiguration exclusion.
    testImplementation(libs.spring.boot.starter.security)
}
