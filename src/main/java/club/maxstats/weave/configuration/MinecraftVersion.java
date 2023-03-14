package club.maxstats.weave.configuration;

import club.maxstats.weave.WeavePlugin;
import club.maxstats.weave.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.InputStream;

@AllArgsConstructor
public enum MinecraftVersion {

    V1_7_10 ("1.7.10", "lunar_named_b2_1.7.10.xsrg"),
    V1_8_9  ("1.8.9",  "lunar_named_b2_1.8.9.xsrg"),
    V1_12_2 ("1.12.2", "lunar_named_b2_1.12.2.xsrg");

    @Getter
    private final String id;
    private final String mappings;

    /**
     * @return {@link #mappings} as a stream.
     */
    public InputStream getMappingStream() {
        return WeavePlugin.class.getResourceAsStream("/mappings/" + mappings);
    }

    /**
     * @return The gradle cache directory corresponding to its version.
     * @see Constants#CACHE_DIR
     */
    public File getCacheDirectory() {
        return new File(Constants.CACHE_DIR, id);
    }

    /**
     * @return The Minecraft jar file in it's cached directory.
     * @see #getCacheDirectory()
     */
    public File getMinecraftJarCache() {
        return new File(getCacheDirectory(), "client.jar");
    }

    /**
     * Converts the version String into a {@link MinecraftVersion} enum.
     *
     * @param version The version String.
     * @return The {@link MinecraftVersion} corresponding {@code version}.
     * @throws IllegalArgumentException If there is no version which corresponds with the {@link #id}.
     */
    public static MinecraftVersion fromString(String version) {
        for (MinecraftVersion ver : values()) {
            if (ver.id.equals(version)) return ver;
        }

        /* In the case that the ID doesn't correspond with the inputted version. */
        throw new IllegalArgumentException(version);
    }

}
