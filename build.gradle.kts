// Root project is a pure aggregator. Shared build configuration will move into
// convention plugins under `build-logic/` in Phase 1 of the modularization.
// Plugins are declared here (apply false) so their versions resolve once from
// the version catalog and subprojects can apply them by alias.
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.detekt) apply false
}

allprojects {
    group = "com.ianarbuckle"
    version = "0.0.1-SNAPSHOT"

    repositories { mavenCentral() }
}