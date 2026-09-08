import io.gitlab.arturbosch.detekt.getSupportedKotlinVersion
import org.gradle.api.Project

/**
 * Pins the Kotlin dependencies on the `detekt` configuration to the version
 * Detekt was compiled against — Detekt fails at runtime if it runs on a
 * different Kotlin version than it was built with.
 *
 * Must be called *after* any plugin that force-manages Kotlin versions across
 * all configurations (e.g. Spring's `io.spring.dependency-management`), because
 * the last-registered resolution rule wins.
 */
internal fun Project.pinDetektKotlinVersion() {
    configurations
        .matching { it.name == "detekt" }
        .all {
            resolutionStrategy.eachDependency {
                if (requested.group == "org.jetbrains.kotlin") {
                    useVersion(getSupportedKotlinVersion())
                }
            }
        }
}