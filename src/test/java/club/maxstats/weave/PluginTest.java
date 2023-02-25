package club.maxstats.weave;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PluginTest {

    @Test
    public void greetingTest() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("club.maxstats.weave");

        assertTrue(project.getPluginManager().hasPlugin("club.maxstats.weave"));
        assertNotNull(project.getTasks().getByName("setupDecompWorkspace"));
    }
}
