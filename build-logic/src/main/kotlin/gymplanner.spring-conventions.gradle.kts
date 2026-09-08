import org.gradle.accessors.dm.LibrariesForLibs

// Convention for modules that contain Spring components: layers kotlin-spring
// (all-open) and Spring dependency management (with the Spring Boot BOM) on top
// of the base Kotlin library convention, plus the shared Kotlin/Jackson runtime.
plugins {
    id("gymplanner.kotlin-library")
    id("org.jetbrains.kotlin.plugin.spring")
    id("io.spring.dependency-management")
}

val libs = the<LibrariesForLibs>()

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}")
    }
}

dependencies {
    "implementation"(libs.jackson.module.kotlin)
    "implementation"(libs.jackson.datatype.jsr310)
    "implementation"(libs.reactor.kotlin)
    "implementation"(libs.kotlin.reflect)
    "implementation"(libs.kotlinx.coroutines.reactor)
}

pinDetektKotlinVersion()