package club.maxstats.weave.configuration;

import org.gradle.api.Project;

public class WeaveMinecraftExtension {

    private String version = "";

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
     * @param version The version of Minecraft to use.
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
