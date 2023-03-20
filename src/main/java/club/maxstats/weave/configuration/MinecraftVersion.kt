package club.maxstats.weave.configuration

import club.maxstats.weave.WeavePlugin
import club.maxstats.weave.util.Constants
import java.io.File
import java.io.InputStream

enum class MinecraftVersion(val id: String, val mappings: String) {

    V1_7_10 ("1.7.10", "lunar_named_b2_1.7.10.xsrg"),
    V1_8_9  ("1.8.9",  "lunar_named_b2_1.8.9.xsrg"),
    V1_12_2 ("1.12.2", "lunar_named_b2_1.12.2.xsrg");

    val mappingStream: InputStream get() =
        WeavePlugin::class.java.getResourceAsStream("/mappings/$mappings") ?: error("Mappings not present: $mappings")

    /**
     * The gradle cache directory corresponding to its version.
     * @see Constants.CACHE_DIR
     */
    val cacheDirectory    = File(Constants.CACHE_DIR, id)

    /**
     * The Minecraft jar file in it's cached directory.
     * @see [cacheDirectory]
     */
    val minecraftJarCache = File(cacheDirectory, "client.jar")

    companion object {

        /**
         * Converts the version String into a [MinecraftVersion] enum.
         *
         * @param version The version String.
         * @return The [MinecraftVersion] corresponding `version`.
         * @throws IllegalArgumentException If there is no version which corresponds with the [id].
         */
        @JvmStatic
        fun fromString(version: String) =
            enumValues<MinecraftVersion>().find { it.id == version } ?: error("No such version: $version")

    }

}
