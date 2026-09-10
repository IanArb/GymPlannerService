// Booking feature. Depends only on :domain and :core-utils — every cross-feature
// need is inverted behind a port implemented by an adapter in :app:
//   PersonalTrainerGateway (trainers), AvailabilityGateway (availability),
//   UserProfileGateway (user-profile), UserGateway (authentication) and
//   NotificationSender (push-notifications). So :booking depends on no other feature.
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
    // DataProviders (Booking/Availability/User/...) live in :domain test fixtures.
    testImplementation(testFixtures(project(":domain")))
}
