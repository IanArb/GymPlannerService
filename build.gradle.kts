plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get()
    id("com.diffplug.spotless") version libs.versions.spotless.get()
    id("io.gitlab.arturbosch.detekt") version libs.versions.detekt.get()
}

group = "com.ianarbuckle"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

repositories {
    mavenCentral()
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config = files("detekt.yml")
    buildUponDefaultConfig = true
    allRules = false
    source = files("src/main/kotlin", "src/test/kotlin")

    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(false)
    }

    baseline = file("$rootDir/detekt-baseline.xml") // Optional: Suppress existing issues
}

dependencies {
    implementation(libs.spring.boot.starter.data.mongodb)
    implementation(libs.spring.boot.starter.data.mongodb.reactive)
    implementation(libs.spring.boot.starter.hateoas)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.reactor.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.reactor)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    developmentOnly(libs.spring.boot.devtools)

    implementation(libs.springdoc.openapi.webflux.ui)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.reactor.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.turbine.test)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockk)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.flapdoodle.mongo)

    detektPlugins(libs.detekt.formatting)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint("1.3.1")
    }
}

dependencyManagement {
    configurations.matching { it.name == "detekt" }.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(io.gitlab.arturbosch.detekt.getSupportedKotlinVersion())
            }
        }
    }
}