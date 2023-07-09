package net.weavemc.gradle.configuration

import org.gradle.api.provider.Property

/**
 * Interface representing the extension of the `minecraft` block in the `build.gradle` file.
 */
interface WeaveMinecraftExtension {

    /**
     * Property representing the Minecraft version.
     */
    val version: Property<MinecraftVersion>

    /**
     * Sets the Minecraft version based on the [versionString].
     *
     * @param versionString The version string representing the Minecraft version.
     */
    fun version(versionString: String) = version.set(MinecraftVersion.fromString(versionString))
}
