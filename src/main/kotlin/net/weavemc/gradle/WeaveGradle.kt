package net.weavemc.gradle

import kotlinx.serialization.encodeToString
import net.weavemc.gradle.configuration.WeaveMinecraftExtension
import net.weavemc.gradle.configuration.pullDeps
import net.weavemc.gradle.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

const val WEAVE_EXTENSION = "weave"
const val WEAVE_MOD_FILE = "weave.mod.json"

/**
 * Gradle build system plugin used to automate the setup of a modding environment.
 */
class WeaveGradle : Plugin<Project> {
    /**
     * [Plugin.apply]
     *
     * @param project The target project.
     */
    override fun apply(project: Project) {
        // Applying our default plugins
        project.pluginManager.apply(JavaLibraryPlugin::class)

        val ext = project.extensions.create(WEAVE_EXTENSION, WeaveMinecraftExtension::class)

        project.afterEvaluate {
            if (!ext.configuration.isPresent) {
                logger.warn("Configuration is missing, make sure to add a configuration through the `weave \\{}` block!")
                return@afterEvaluate
            }

            if (!ext.version.isPresent) {
                logger.warn("No Minecraft version declared! Set one through the `weave \\{}` block.")
                return@afterEvaluate
            }

            pullDeps(ext.version.get(), ext.configuration.get().namespace)
        }

        project.tasks.withType<ProcessResources>().configureEach {
            outputs.upToDateWhen { false }

            doFirst {
                if (!ext.configuration.isPresent) {
                    return@doFirst
                }

                val config = ext.configuration.get().copy(compiledFor = ext.version.get().versionName)
                destinationDir.resolve(WEAVE_MOD_FILE).writeText(Constants.JSON.encodeToString(config))
            }
        }
    }
}
