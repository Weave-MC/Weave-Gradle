package club.maxstats.weave.configuration.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import org.gradle.api.Project;

@AllArgsConstructor
public class MinecraftLibraryProvider {

    private final MinecraftProvider minecraftProvider;

    public void provide() {
        JsonArray librariesArray = this.minecraftProvider.getVersionJson().get("libraries").getAsJsonArray();
        Project   project        = this.minecraftProvider.getProject();

        project.getRepositories().maven(mavenArtifactRepository -> {
            mavenArtifactRepository.setName("mojang");
            mavenArtifactRepository.setUrl("https://libraries.minecraft.net/");
        });

        for (JsonElement library : librariesArray) {
            String name = library.getAsJsonObject().get("name").getAsString();

            if (name == null || name.contains("twitch-platform") || name.contains("twitch-external")) continue;

            project.getDependencies().add("compileOnly", name);
        }
    }

}
