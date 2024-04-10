package net.weavemc.gradle

import com.grappenmaker.mappings.remapJar
import kotlinx.serialization.encodeToString
import net.weavemc.gradle.configuration.WeaveMinecraftExtension
import net.weavemc.gradle.configuration.pullDeps
import net.weavemc.gradle.util.Constants
import net.weavemc.internals.MinecraftVersion
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

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
        project.pluginManager.apply(JavaPlugin::class)

        val ext = project.extensions.create("weave", WeaveMinecraftExtension::class)

        project.afterEvaluate {
            if (!ext.configuration.isPresent) throw GradleException(
                "Configuration is missing, make sure to add a configuration through the weave {} block!"
            )

            if (!ext.version.isPresent) throw GradleException(
                "Set a Minecraft version through the weave {} block!"
            )

            val version = ext.version.getOrElse(MinecraftVersion.V1_8_9)
            pullDeps(version, ext.configuration.get().namespace)
        }

        project.tasks.withType<ProcessResources>().configureEach {
            outputs.upToDateWhen { false }
            doLast {
                val config = ext.configuration.get().copy(compiledFor = ext.version.get().versionName)
                destinationDir.resolve("weave.mod.json").writeText(Constants.JSON.encodeToString(config))
            }
        }
    }
}
