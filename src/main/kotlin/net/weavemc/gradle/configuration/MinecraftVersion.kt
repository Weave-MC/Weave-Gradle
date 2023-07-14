package net.weavemc.gradle.configuration

import net.weavemc.gradle.WeavePlugin
import net.weavemc.gradle.util.Constants
import java.io.File
import java.io.InputStream

enum class MinecraftVersion(val id: String, val mappings: String) {
    // mappings
    V1_7_10("1.7.10", "lunar_named_b5_1.7.10.xsrg"),
    V1_8_9("1.8.9", "lunar_named_b5_1.8.9.xsrg"),
    V1_12_2("1.12.2", "lunar_named_b5_1.12.2.xsrg"),
    V1_16_5("1.16.5", "lunar_named_b5_1.16.5.xsrg"),
    V1_17("1.17", "lunar_named_b5_1.17.xsrg"),
    V1_17_1("1.17.1", "lunar_named_b5_1.17.1.xsrg"),
    V1_18_1("1.18.1", "lunar_named_b5_1.18.1.xsrg"),
    V1_18_2("1.18.2", "lunar_named_b5_1.18.2.xsrg"),
    V1_19("1.19", "lunar_named_b5_1.19.xsrg"),
    V1_19_2("1.19.2", "lunar_named_b5_1.19.2.xsrg"),
    V1_19_3("1.19.3", "lunar_named_b5_1.19.3.xsrg"),
    V1_19_4("1.19.4", "lunar_named_b5_1.19.4.xsrg"),
    V1_20("1.20", "lunar_named_b5_1.20.xsrg"),
    V1_20_1("1.20.1", "lunar_named_b5_1.20.1.xsrg");

    val mappingStream: InputStream
        get() =
            WeavePlugin::class.java.getResourceAsStream("/mappings/$mappings")
                ?: error("Mappings not present: $mappings")

    /**
     * The gradle cache directory corresponding to its version.
     * @see Constants.CACHE_DIR
     */
    val cacheDirectory = File(Constants.CACHE_DIR, id)

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
