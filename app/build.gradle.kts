// Shared Kotlin/Spring/Spotless/Detekt/test config comes from the convention
// plugins in build-logic/. Only the executable-app specifics live here.
plugins {
    id("gymplanner.spring-conventions")
    alias(libs.plugins.spring.boot)
}

description = "app"

dependencies {
    implementation(libs.spring.boot.starter.data.mongodb.reactive)
    implementation(libs.spring.boot.starter.hateoas)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    developmentOnly(libs.spring.boot.devtools)

    implementation(libs.springdoc.openapi.webflux.ui)

    implementation(libs.firebase.admin)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.spring.boot.starter.data.mongodb.test)
    testImplementation(libs.reactor.test)
    testImplementation(libs.flapdoodle.mongo)
}
