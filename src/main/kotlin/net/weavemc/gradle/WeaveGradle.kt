package net.weavemc.gradle

import com.grappenmaker.mappings.remapJar
import kotlinx.serialization.encodeToString
import net.weavemc.gradle.configuration.WeaveMinecraftExtension
import net.weavemc.gradle.configuration.pullDeps
import net.weavemc.gradle.util.Constants
import net.weavemc.gradle.util.mappedJarCache
import net.weavemc.gradle.util.minecraftJarCache
import net.weavemc.internals.MappingsRetrieval
import net.weavemc.internals.MinecraftVersion
import net.weavemc.internals.ModConfig
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File

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

        val ext = project.extensions.create("minecraft", WeaveMinecraftExtension::class)

        project.afterEvaluate {
            if (!ext.configuration.isPresent) throw GradleException(
                "Configuration is missing, make sure to add a configuration through the minecraft {} block!"
            )

            if (!ext.version.isPresent) throw GradleException(
                "Set a Minecraft version through the minecraft {} block!"
            )

            val version = ext.version.getOrElse(MinecraftVersion.V1_8_9)
            pullDeps(version, ext.configuration.get().namespace)
        }

        project.tasks.withType<ProcessResources>().configureEach {
            doLast {
                val config = ext.configuration.get().copy(compiledFor = ext.version.get().versionName)
                destinationDir.resolve("weave.mod.json").writeText(Constants.JSON.encodeToString(config))
            }
        }

        val remapJarTask = project.tasks.register("remapJar", RemapJarTask::class.java) {
            val version = ext.version.getOrElse(MinecraftVersion.V1_8_9)
            minecraftJar = version.mappedJarCache(ext.configuration.get().namespace)
            inputJar = project.tasks["jar"].outputs.files.singleFile
            outputJar = inputJar.parentFile.resolve("${inputJar.nameWithoutExtension}-mapped.${inputJar.extension}")
        }

        project.tasks.named("assemble") { finalizedBy(remapJarTask) }
    }

    /**
     * Remaps the jar back to vanilla mappings (obfuscated)
     * The jar will then be mapped when loaded by Weave-Loader
     */
    open class RemapJarTask: DefaultTask() {
        @Internal
        lateinit var minecraftJar: File

        @InputFile
        lateinit var inputJar: File

        @OutputFile
        lateinit var outputJar: File

        @TaskAction
        fun remap() {
            val ext = project.extensions["minecraft"] as WeaveMinecraftExtension
            val version = ext.version.get()
            val fullMappings = version.loadMergedMappings()

            val mid = ext.configuration.get().namespace
            require(mid in fullMappings.namespaces) {
                "Namespace $mid is not available in mappings! Available namespaces are: ${fullMappings.namespaces}"
            }

            remapJar(fullMappings, inputJar, outputJar, mid, "official", files = listOf(minecraftJar))
        }
    }
}

fun MinecraftVersion.loadMergedMappings() =
    MappingsRetrieval.loadMergedWeaveMappings(versionName, minecraftJarCache).mappings
