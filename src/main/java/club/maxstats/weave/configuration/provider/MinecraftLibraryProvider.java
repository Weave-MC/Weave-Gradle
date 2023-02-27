package club.maxstats.weave.configuration.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.gradle.api.Project;

public class MinecraftLibraryProvider {
    private MinecraftProvider minecraftProvider;
    public MinecraftLibraryProvider(MinecraftProvider minecraftProvider) {
        this.minecraftProvider = minecraftProvider;
    }

    public void provide() {
        JsonArray librariesArray = this.minecraftProvider.getVersionJson().get("libraries").getAsJsonArray();
        Project project = this.minecraftProvider.getProject();

        project.getRepositories().maven(mavenArtifactRepository -> {
            mavenArtifactRepository.setName("mojang");
            mavenArtifactRepository.setUrl("https://libraries.minecraft.net/");
        });

        for (JsonElement library : librariesArray) {
            String name = library.getAsJsonObject().get("name").getAsString();

            if (name == null || name.contains("twitch-platform") || name.contains("twitch-external"))
                continue;

            project.getDependencies().add("compileOnly", name);
        }
    }
}
