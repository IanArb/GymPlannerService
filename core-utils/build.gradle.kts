// Shared, low-level building blocks with no dependency on any feature module:
// the GymLocation enum, Kotlin/coroutine extensions, the @FutureDate validation
// annotation, and the global (WebFlux) exception handler.
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.validation)
}
