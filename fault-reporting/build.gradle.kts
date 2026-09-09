// Fault-reporting feature: self-contained vertical slice (controller + service +
// MongoDB document/repository + exception) with no dependency on other features.
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.data.mongodb.reactive)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi.webflux.ui)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    // Only needed so the @WebFluxTest can reference ReactiveWebSecurityAutoConfiguration
    // in its excludeAutoConfiguration (security is not otherwise used by this feature).
    testImplementation(libs.spring.boot.starter.security)
}
