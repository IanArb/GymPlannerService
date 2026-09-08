pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "GymPlannerService"

include(":app")
include(":core-utils")
include(":security")