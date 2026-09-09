// Exercises feature: a self-contained vertical slice (controller + service +
// MongoDB document/repository) with no dependency on any other feature module.
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.data.mongodb.reactive)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi.webflux.ui)
}
