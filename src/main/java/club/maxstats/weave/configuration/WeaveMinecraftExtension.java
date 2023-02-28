package club.maxstats.weave.configuration;

import org.gradle.api.Project;

public class WeaveMinecraftExtension {

    private String version = "";
    private String mappings = "";
    
    static WeaveMinecraftExtension get(Project project) {
        return (WeaveMinecraftExtension) project.getExtensions().getByName("minecraft");
    }

    public String getVersion() { 
        return this.version; 
    }
    
    public String getMappings() { 
        return this.mappings;
    }

    public void setVersion(String version) { this.version = version; }
    public void setMappings(String mappings) { this.mappings = mappings; }

}
