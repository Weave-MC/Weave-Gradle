package club.maxstats.weave.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * TODO: Add description here.
 * @author Scherso (<a href="https://github.com/Scherso">...</a>), Max (<a href="https://github.com/exejar">...</a>)
 */
public class SetupTask extends DefaultTask {

    public SetupTask() {
        super();
    }

    /**
     * Sets up the decompiled workspace for the user in their development environment.
     */
    @TaskAction
    public void setupDecompiledWorkspace() {

    }

}
