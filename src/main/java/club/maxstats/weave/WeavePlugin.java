package club.maxstats.weave;

import club.maxstats.weave.configuration.DependencyManager;
import club.maxstats.weave.configuration.WeaveMinecraftExtension;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

/**
 * TODO: Add description here.
 *
 * @author Scherso (<a href="https://github.com/Scherso">...</a>), Max (<a href="https://github.com/exejar">...</a>)
 * Nils <3 (<a href="https://github.com/Nilsen84">...</a>)
 * @version 1.0.0
 * @since 1.0.0
 */
public class WeavePlugin implements Plugin<Project> {

    /**
     * {@link Plugin#apply(Object)}
     *
     * @param project The target project.
     */
    @Override
    public void apply(@NonNull Project project) {
        /* Applying our default plugins. */
        project.getPluginManager().apply(JavaPlugin.class);
        project.getRepositories().mavenCentral();
        project.getRepositories().mavenLocal();

        WeaveMinecraftExtension ext = project.getExtensions().create("minecraft", WeaveMinecraftExtension.class);

        project.afterEvaluate(proj -> {
            new DependencyManager(proj, ext.getVersion().get()).pullDeps();
        });
    }

}
