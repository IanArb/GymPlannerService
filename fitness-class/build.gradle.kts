// Fitness-classes feature. The reminder scheduler's cross-feature needs are inverted
// behind ports implemented by adapters in :app: UserGateway (authentication, for the
// push tokens) and NotificationSender (push-notifications, to notify attendees). So
// this feature depends on no other feature module.
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
    // Only for the @WebFluxTest's ReactiveWebSecurityAutoConfiguration exclusion.
    testImplementation(libs.spring.boot.starter.security)
    // FitnessClassDataProvider lives in :domain test fixtures.
    testImplementation(testFixtures(project(":domain")))
}
