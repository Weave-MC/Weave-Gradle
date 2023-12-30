package net.weavemc.gradle

import com.grappenmaker.mappings.*
import net.weavemc.gradle.configuration.*
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
import org.objectweb.asm.commons.SimpleRemapper
import java.io.File
import java.util.jar.JarFile

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
        val mappings = ext.mappings.getOrElse(MinecraftMappings.MCP)

        project.afterEvaluate {
            pullDeps(version, mappings)
        }

        val remapJarTask = project.tasks.register("remapJar", RemapJarTask::class.java) {
            minecraftJar = JarFile("${version.cacheDirectory}${File.separator}client-${mappings.id}.jar")
            inputJar = project.tasks["jar"].outputs.files.singleFile
            outputJar = inputJar.parentFile.resolve("${inputJar.nameWithoutExtension}-mapped.${inputJar.extension}")
        }

        project.tasks.named("assemble") {
            finalizedBy(remapJarTask)
        }
    }

    /**
     * Remaps the jar back to vanilla mappings (obfuscated)
     * The jar will then be mapped when loaded by Weave-Loader
     */
    open class RemapJarTask: DefaultTask() {
        @Internal
        lateinit var minecraftJar: JarFile

        @InputFile
        lateinit var inputJar: File

        @OutputFile
        lateinit var outputJar: File

        @TaskAction
        fun remap() {
            val ext = this.project.extensions["minecraft"] as WeaveMinecraftExtension
            val fullMappings =
                MappingsLoader.loadMappings(ext.mappings.get().mappingsStream(ext.version.get()).toLines())

            val names = fullMappings.asASMMapping(
                from = "named",
                to = "official",
                includeMethods = false,
                includeFields = false
            )
            val mapper = SimpleRemapper(names)
            val cache = hashMapOf<String, ByteArray?>()

            val lookup = minecraftJar.entries().asSequence().filter { it.name.endsWith(".class") }
                .map { it.name.dropLast(6) to { minecraftJar.getInputStream(it).readBytes() } }.toMap()

            remapModJar(fullMappings, inputJar, outputJar, "named", "official") { name ->
                val mappedName = names[name] ?: name
                if (mappedName in lookup) cache.getOrPut(mappedName) { lookup.getValue(mappedName)().remap(mapper) }
                else null
            }
        }
    }
}
