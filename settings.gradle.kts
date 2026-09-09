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
include(":authentication")
include(":exercises")
include(":fault-reporting")
include(":gym-locations")
include(":messages")
include(":trainers")
include(":facility-status")
include(":checkin")
include(":push-notifications")
include(":fitness-class")