package net.weavemc.gradle.configuration

import net.weavemc.gradle.WeaveGradle
import net.weavemc.gradle.util.Constants
import org.gradle.api.provider.Property
import java.io.File
import java.io.InputStream

interface WeaveMinecraftExtension {
    val version: Property<MinecraftVersion>
    val mappings: Property<MinecraftMappings>

    fun version(versionString: String) = version.set(MinecraftVersion.fromString(versionString))
    fun mappings(mappingString: String) = mappings.set(MinecraftMappings.fromString(mappingString))
}

enum class MinecraftMappings(val id: String) {
    MOJANG("mojang"),
    MCP("mcp"),
    YARN("yarn");

    fun mappingsStream(version: MinecraftVersion): InputStream {
        return if (this == MOJANG) {
            File(version.cacheDirectory, "mojang.mappings").inputStream()
        } else {
            WeaveGradle::class.java.getResourceAsStream("/mappings/$id/${version.id}")
                ?: error("Mappings not present: $id/$version")
        }
    }


    companion object {
        /**
         * Converts the mapping String into a [MinecraftMappings] enum.
         *
         * @param mappings The mappings String.
         * @return The [MinecraftMappings] corresponding `version`.
         * @throws IllegalArgumentException If there is no mappings which corresponds with the [id].
         */
        @JvmStatic
        fun fromString(mappings: String) =
            enumValues<MinecraftMappings>().find { it.id == mappings } ?: error("No such mappings: $mappings")
    }
}
enum class MinecraftVersion(val id: String) {
    V1_7("1.7"),
    V1_8("1.8"),
    V1_12("1.12"),
    V1_16("1.16"),
    V1_17("1.17"),
    V1_18("1.18"),
    V1_19("1.19"),
    V1_20("1.20");

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
