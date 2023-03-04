package club.maxstats.weave.configuration;

import org.gradle.api.Project;

public class WeaveMinecraftExtension {

    private String version = "";
    private String mappings = "";

    static WeaveMinecraftExtension get(Project project) {
        return (WeaveMinecraftExtension) project.getExtensions().getByName("minecraft");
    }

    /**
     * @return the version Minecraft set by the user.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * @return The mappings set by the user. ex. "stable_22"
     */
    public String getMappings() {
        return this.mappings;
    }

    /**
     * @param version The version of Minecraft to use.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @param mappings The mappings to use. ex. "stable_22"
     */
    public void setMappings(String mappings) {
        this.mappings = mappings;
    }

}
