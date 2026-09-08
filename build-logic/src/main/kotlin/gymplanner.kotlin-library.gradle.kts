import org.gradle.accessors.dm.LibrariesForLibs

// Base convention shared by every module: Kotlin JVM toolchain, compiler args,
// Spotless (ktlint), Detekt (root config/baseline), and the common test stack.
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
}

val libs = the<LibrariesForLibs>()

kotlin {
    jvmToolchain(22)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xannotation-default-target=param-property",
        )
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.from(rootProject.file("detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
    source.from(files("src/main/kotlin", "src/test/kotlin"))
    baseline = rootProject.file("detekt-baseline.xml")

    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(false)
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
    }
}

pinDetektKotlinVersion()

dependencies {
    "detektPlugins"(libs.detekt.formatting)

    "testImplementation"(libs.kotlin.test.junit5)
    "testImplementation"(libs.turbine.test)
    "testImplementation"(libs.truth)
    "testImplementation"(libs.mockk)
    "testImplementation"(libs.kotlinx.coroutines.test)
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }