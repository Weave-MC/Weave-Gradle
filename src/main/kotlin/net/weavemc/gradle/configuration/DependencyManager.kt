package net.weavemc.gradle.configuration

import com.grappenmaker.mappings.remapJar
import kotlinx.serialization.Serializable
import net.weavemc.gradle.loadMergedMappings
import net.weavemc.gradle.util.Constants
import net.weavemc.gradle.util.DownloadUtil
import net.weavemc.gradle.util.mappedJarCache
import net.weavemc.gradle.util.minecraftJarCache
import net.weavemc.internals.MinecraftVersion
import org.gradle.api.Project
import java.net.URL

private inline fun <reified T> String?.decodeJSON() =
    if (this != null) Constants.JSON.decodeFromString<T>(this) else null

/**
 * Pulls dependencies from [addMinecraftAssets] and [addMappedMinecraft]
 */
fun Project.pullDeps(version: MinecraftVersion, namespace: String) {
    addMinecraftAssets(version)
    addMappedMinecraft(version, namespace)
}

/**
 * Adds Minecraft as a dependency by providing the jar to the projects file tree.
 */
private fun Project.addMinecraftAssets(version: MinecraftVersion) {
    val manifest = DownloadUtil.fetch(Constants.VERSION_MANIFEST).decodeJSON<VersionManifest>() ?: return
    val versionEntry = manifest.versions.find { it.id == version.versionName } ?: return
    val versionInfo = DownloadUtil.fetch(versionEntry.url).decodeJSON<VersionInfo>() ?: return

    val client = versionInfo.downloads.client
    DownloadUtil.checksumAndDownload(URL(client.url), client.sha1, version.minecraftJarCache.toPath())

    repositories.maven {
        name = "mojang"
        setUrl("https://libraries.minecraft.net/")
    }

    versionInfo.libraries.filter { "twitch-platform" !in it.name && "twitch-external" !in it.name }
        .forEach { dependencies.add("compileOnly", it.name) }
}

private fun Project.addMappedMinecraft(version: MinecraftVersion, namespace: String) = runCatching {
    val mapped = version.mappedJarCache(namespace)
    if (!mapped.exists()) {
        val fullMappings = version.loadMergedMappings()
        remapJar(fullMappings, version.minecraftJarCache, mapped, to = namespace)
    }

    dependencies.add("compileOnly", project.files(mapped))
}.onFailure { it.printStackTrace() }

@Serializable
private data class VersionManifest(val versions: List<ManifestVersion>)

@Serializable
private data class ManifestVersion(val id: String, val url: String)

@Serializable
private data class VersionInfo(val downloads: VersionDownloads, val libraries: List<Library>)

@Serializable
private data class VersionDownloads(val client: VersionDownload)

@Serializable
private data class VersionDownload(val url: String, val sha1: String)

@Serializable
private data class Library(val name: String)
