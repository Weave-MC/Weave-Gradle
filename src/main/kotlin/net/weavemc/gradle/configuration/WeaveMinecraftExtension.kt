package net.weavemc.gradle.configuration

import net.weavemc.gradle.WeaveGradle
import net.weavemc.gradle.util.Constants
import net.weavemc.internals.MappingsType
import net.weavemc.internals.MinecraftVersion
import org.gradle.api.provider.Property
import java.io.File
import java.io.InputStream

interface WeaveMinecraftExtension {
    val version: Property<MinecraftVersion>
    val mappings: Property<MappingsType>

    fun version(versionString: String) = version.set(
        MinecraftVersion.fromVersionName(versionString) ?: error("Unknown version $versionString")
    )

    fun mappings(mappingString: String) = mappings.set(MappingsType.fromString(mappingString))
}
