// Fitness-classes feature. Depends on :authentication (UserRepository, for the
// reminder scheduler) and :push-notifications (FcmSender, to notify attendees).
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":authentication"))
    implementation(project(":push-notifications"))

    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.data.mongodb.reactive)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi.webflux.ui)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    // Only for the @WebFluxTest's ReactiveWebSecurityAutoConfiguration exclusion.
    testImplementation(libs.spring.boot.starter.security)
}
