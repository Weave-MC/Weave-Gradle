package club.maxstats.weave.configuration.provider;

import org.gradle.api.Project;

public class MappingsProvider {

    private String version;
    private String downloadPath;
    private Project project;

    public MappingsProvider(Project project, String version) {
        this.project = project;
        this.version = version;
    }

    public void provide() {

    }
}
