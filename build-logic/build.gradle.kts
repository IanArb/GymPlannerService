plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Plugin artifacts the convention plugins apply via `plugins { id(...) }`.
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.allopen.plugin)
    implementation(libs.spring.dependency.management.plugin)
    implementation(libs.spotless.plugin)
    implementation(libs.detekt.plugin)

    // Exposes the generated `libs` type-safe accessors (LibrariesForLibs) to the
    // precompiled convention plugins.
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}