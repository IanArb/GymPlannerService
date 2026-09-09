// Shared Kotlin/Spring/Spotless/Detekt/test config comes from the convention
// plugins in build-logic/. Only the executable-app specifics live here.
plugins {
    id("gymplanner.spring-conventions")
    alias(libs.plugins.spring.boot)
}

description = "app"

dependencies {
    implementation(project(":core-utils"))
    implementation(project(":security"))
    implementation(project(":authentication"))
    implementation(project(":exercises"))
    implementation(project(":fault-reporting"))
    implementation(project(":gym-locations"))
    implementation(project(":messages"))
    implementation(project(":trainers"))
    implementation(project(":facility-status"))
    implementation(project(":checkin"))
    implementation(project(":push-notifications"))
    implementation(project(":fitness-class"))
    implementation(project(":availability"))
    implementation(project(":booking"))

    implementation(libs.spring.boot.starter.data.mongodb.reactive)
    implementation(libs.spring.boot.starter.hateoas)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // developmentOnly doesn't extend implementation, so it needs the BOM directly.
    developmentOnly(platform("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}"))
    developmentOnly(libs.spring.boot.devtools)

    implementation(libs.springdoc.openapi.webflux.ui)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.spring.boot.starter.data.mongodb.test)
    testImplementation(libs.reactor.test)
    testImplementation(libs.flapdoodle.mongo)
    // The user-profile tests (still in :app, Tier 5) use UserProfileDataProvider
    // (fixture in :authentication).
    testImplementation(testFixtures(project(":authentication")))
}
