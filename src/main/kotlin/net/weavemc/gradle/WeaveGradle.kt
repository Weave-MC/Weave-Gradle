package net.weavemc.gradle

import net.weavemc.gradle.configuration.*
import net.weavemc.gradle.mapping.loadMappings
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.InputFile
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
        project.repositories.mavenCentral()
        project.repositories.mavenLocal()

        val ext = project.extensions.create("weavecraft", WeaveMinecraftExtension::class)
        project.afterEvaluate {
            pullDeps(ext.version.get(), ext.mappings.getOrElse(MinecraftMappings.YARN))
        }

        val remapJarTask = project.tasks.register("remapJar", RemapJarTask::class.java) {
            inputJar = project.tasks["jar"].outputs.files.singleFile
            outputJar = File(project.buildDir, "${project.name}-${project.version}.jar")
        }

//        Remapping Mixin annotations is annoying
//        project.tasks.named("jar") {
//            finalizedBy(remapJarTask)
//        }
    }

    /**
     * Remaps the jar back to vanilla mappings (obfuscated)
     * The jar will then be mapped when loaded by Weave-Loader
     */
    open class RemapJarTask: DefaultTask() {
        @InputFile
        lateinit var inputJar: File
        @OutputFile
        lateinit var outputJar: File

        @TaskAction
        fun remap() {
            val ext = this.project.extensions["weavecraft"] as WeaveMinecraftExtension
            val fullMappings = loadMappings(ext.mappings.get().mappingsStream(ext.version.get()).toLines())

            remapJar(fullMappings, inputJar, outputJar, "named", "official")
        }
    }
}
