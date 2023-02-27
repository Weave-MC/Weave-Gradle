package club.maxstats.weave;

import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Map;

/**
 * TODO: Add description here.
 *
 * @author Scherso (<a href="https://github.com/Scherso">...</a>), Max (<a href="https://github.com/exejar">...</a>)
 *         Nils <3 (<a href="https://github.com/Nilsen84">...</a>)
 * @version 1.0.0
 * @since 1.0.0
 */
public class WeavePlugin extends AbstractPlugin {

    /**
     * {@link Plugin#apply(Object)}
     *
     * @param project The target project.
     */
    @Override
    public void apply(@NonNull Project project) {
        /* Applying our default plugins. */
        project.apply(Map.of("plugin", "idea"));
        project.apply(Map.of("plugin", "eclipse"));
        project.apply(Map.of("plugin", "java"));

        super.apply(project);
    }

}
