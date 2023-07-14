package net.weavemc.gradle

import net.weavemc.gradle.configuration.MinecraftExtension
import net.weavemc.gradle.configuration.pullDeps
import org.cubewhy.launcher.LunarClient
import org.cubewhy.launcher.LunarDir
import org.cubewhy.launcher.game.MinecraftArgs
import org.cubewhy.lunarcn.JavaAgent
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

/**
 * Gradle build system plugin used to automate the setup of a modding environment.
 *
 * @author Scherso ([...](https://github.com/Scherso)), Max ([...](https://github.com/exejar))
 *         Nils <3 ([...](https://github.com/Nilsen84)), NotEvenJoking ([...](https://github.com/770grappenmaker))
 * @version 1.0.0
 * @since 1.0.0
 */
class WeavePlugin : Plugin<Project> {

    /**
     * @param project The target project.
     * @see Plugin.apply
     */
    override fun apply(project: Project) {
        // Applying our default plugins
        project.apply<JavaPlugin>()
        project.repositories.mavenCentral()

        val minecraftExtension = project.extensions.create("minecraft", MinecraftExtension::class)
        project.afterEvaluate { pullDeps(minecraftExtension.version.get()) }

        project.tasks.register("runClient").apply {
            val version = minecraftExtension.version.get().id
            val baseDir = LunarDir.lunarDir.absolutePath + "/offline/multiver"
            val texturesDir = LunarDir.lunarDir.absolutePath + "/textures"
            val mcArgs = MinecraftArgs(minecraftExtension.runDir.get(), texturesDir, 300, 400)
            val javaExec = System.getProperty("java.home") + "/bin/java"
            val jvmArgs = minecraftExtension.jvmArgs.get()
            val programArgs = minecraftExtension.programArgs.get()
            val agents = arrayOf(
                JavaAgent(project.buildFile.absolutePath)
            )
            val args =
                LunarClient.getArgs(
                    version,
                    "lunar",
                    "master",
                    baseDir,
                    mcArgs,
                    javaExec,
                    jvmArgs,
                    programArgs,
                    agents,
                    true
                )
            Runtime.getRuntime().exec(args) // start the game
        }
    }
}
