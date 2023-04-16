package net.weavemc.gradle.configuration

import org.gradle.api.provider.Property

interface WeaveMinecraftExtension {
    val version: Property<MinecraftVersion>
    fun version(versionString: String) = version.set(MinecraftVersion.fromString(versionString))
}
