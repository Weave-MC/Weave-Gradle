package club.maxstats.weave;

import club.maxstats.weave.task.SetupTask;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

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
     * @param target The target object.
     */
    @Override
    public void apply(@NonNull Project target) {
        /* Applying our method in 'AbstractPlugin#apply(Project)'. */
        super.apply(target);

        /* Initializing tasks. */
        makeTask("setupDecompWorkspace", SetupTask.class).doLast(task -> {
            System.out.println("Hello, World?");
        });
    }

}
