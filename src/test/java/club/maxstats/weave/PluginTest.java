package club.maxstats.weave;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PluginTest {

    /**
     * Plugin Test with {@link Project} and JUNIT-5.
     *
     * @see <a href="https://junit.org/junit5/docs/current/user-guide/">JUNIT-5 User Documentation</a>
     */
    @Test
    public void greetingTest() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("club.maxstats.weave");

        assertTrue(project.getPluginManager().hasPlugin("club.maxstats.weave"));
//      assertNotNull(project.getTasks().getByName("setupDecompWorkspace"));
    }

}
