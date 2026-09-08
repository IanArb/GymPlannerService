import org.gradle.accessors.dm.LibrariesForLibs

// Convention for modules that contain Spring components: layers kotlin-spring
// (all-open) plus the Spring Boot dependency BOM (via Gradle's native platform()
// support) and the shared Kotlin/Jackson runtime on top of kotlin-library.
//
// NOTE: we deliberately do NOT use the io.spring.dependency-management plugin.
// It applies managed versions to *every* configuration — including Kotlin's
// `kotlinCompilerPluginClasspath` — which corrupts the all-open compiler plugin
// classpath and makes the Kotlin Build Tools API fail with
// "getPluginClasspaths() is null". platform() only constrains the dependency
// configurations, leaving the compiler-plugin classpath untouched.
plugins {
    id("gymplanner.kotlin-library")
    id("org.jetbrains.kotlin.plugin.spring")
}

val libs = the<LibrariesForLibs>()

dependencies {
    val bom = platform("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}")
    "implementation"(bom)
    "testImplementation"(bom)

    "implementation"(libs.jackson.module.kotlin)
    "implementation"(libs.jackson.datatype.jsr310)
    "implementation"(libs.reactor.kotlin)
    "implementation"(libs.kotlin.reflect)
    "implementation"(libs.kotlinx.coroutines.reactor)
}