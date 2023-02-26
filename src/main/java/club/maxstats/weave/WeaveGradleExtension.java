package club.maxstats.weave;

import org.gradle.api.Project;

public interface WeaveGradleExtension {

    static WeaveGradleExtension get(Project project) {
        return (WeaveGradleExtension) project.getExtensions().getByName("weave");
    }

}
