package club.maxstats.weave

import club.maxstats.weave.configuration.WeaveMinecraftExtension
import club.maxstats.weave.configuration.pullDeps
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
        project.afterEvaluate { pullDeps(ext.version.get()) }
    }
}
