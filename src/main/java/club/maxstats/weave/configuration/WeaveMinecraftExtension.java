package club.maxstats.weave.configuration;

import org.gradle.api.Project;

public class WeaveMinecraftExtension {
    private String version = "";

    static WeaveMinecraftExtension get(Project project) {
        return (WeaveMinecraftExtension) project.getExtensions().getByName("minecraft");
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
