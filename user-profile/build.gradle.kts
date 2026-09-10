// User-profile feature. The UserProfile model lives in :domain, and the two ports
// this feature used to implement — authentication's UserProfileRegistrar and
// booking's UserProfileGateway — are now wired by adapters in :app. So this feature
// depends on no other feature module (only :domain and :core-utils).
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
    // UserProfileDataProvider lives in :domain test fixtures.
    testImplementation(testFixtures(project(":domain")))
}
