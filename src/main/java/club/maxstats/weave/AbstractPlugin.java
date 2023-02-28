package club.maxstats.weave;

import club.maxstats.weave.configuration.DependencyManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;

public class AbstractPlugin implements Plugin<Project> {

    protected Project project;

    @Override
    public void apply(Project target) {
        project = target;

        /* Applying a Java-lang Gradle plugin. */
        target.getPluginManager().apply(JavaPlugin.class);

        project.afterEvaluate(project1 -> {
            new DependencyManager(project1).pullDeps();
        });
    }

    /**
     * Creates a task of the specified type.
     *
     * @param target The project to create the task in.
     * @param name   The name of the task.
     * @param type   The type of the task.
     * @return       The created task.
     * @param <T>    Inherited from {@link Task}.
     */
    public <T extends Task> T makeTask(Project target, String name, Class<T> type) {
        return target.getTasks().create(name, type);
    }

    /**
     * Permit to create a Task instance of the type in the project.
     *
     * @param name The name of the task.
     * @param type The type of the task instance.
     * @return     The created task object for the {@link #project}.
     * @param <T>  Inherited from {@link Task}.
     */
    public <T extends Task> T makeTask(String name, Class<T> type) {
        return makeTask(project, name, type);
    }

}
