package net.weavemc.gradle

import com.grappenmaker.mappings.*
import net.weavemc.gradle.configuration.*
import net.weavemc.gradle.util.mappedJarCache
import net.weavemc.gradle.util.minecraftJarCache
import net.weavemc.internals.MappingsRetrieval
import net.weavemc.internals.MappingsType
import net.weavemc.internals.MinecraftVersion
import org.gradle.api.DefaultTask
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
        val version = ext.version.getOrElse(MinecraftVersion.V1_8_9)
        val mappings = ext.mappings.getOrElse(MappingsType.MCP)

        project.afterEvaluate { pullDeps(version, mappings) }
        val remapJarTask = project.tasks.register("remapJar", RemapJarTask::class.java) {
            minecraftJar = version.mappedJarCache(mappings)
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

            val mid = ext.mappings.get().id
            remapJar(fullMappings, inputJar, outputJar, "$mid-named", "official", files = listOf(minecraftJar))
        }
    }
}

fun MinecraftVersion.loadMergedMappings() =
    MappingsRetrieval.loadMergedWeaveMappings(versionName, minecraftJarCache).mappings
