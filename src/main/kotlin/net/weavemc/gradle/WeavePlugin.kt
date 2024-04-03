package net.weavemc.gradle

import net.weavemc.gradle.configuration.WeaveMinecraftExtension
import net.weavemc.gradle.configuration.pullDeps
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

        val ext = project.extensions.create("minecraft", WeaveMinecraftExtension::class)
        project.afterEvaluate { pullDeps(ext.version.get()) }
    }
}
