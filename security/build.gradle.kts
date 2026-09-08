// Cross-cutting security infrastructure: the WebFlux security filter chain, JWT
// utilities, and the reactive authentication manager. It knows nothing about the
// authentication *domain* — user lookup is inverted behind the SecurityUserLookup
// port, which the auth domain implements.
plugins {
    id("gymplanner.spring-conventions")
}

dependencies {
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.webflux)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
}
