package club.maxstats.weave.configuration;

import org.gradle.api.provider.Property;

public interface WeaveMinecraftExtension {

    Property<MinecraftVersion> getVersion();

    default void version(String version) {
        getVersion().set(MinecraftVersion.fromString(version));
    }

}
