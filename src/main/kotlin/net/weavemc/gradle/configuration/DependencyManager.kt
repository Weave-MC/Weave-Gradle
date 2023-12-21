package net.weavemc.gradle.configuration

import com.grappenmaker.mappings.MappingsLoader
import com.grappenmaker.mappings.remapJar
import kotlinx.serialization.SerialName
import net.weavemc.gradle.util.Constants
import net.weavemc.gradle.util.DownloadUtil
import kotlinx.serialization.Serializable
import org.gradle.api.Project
import java.io.File
import java.io.InputStream
import java.net.URL

private inline fun <reified T> String?.decodeJSON() =
    if (this != null) Constants.JSON.decodeFromString<T>(this) else null

/**
 * Pulls dependencies from [addMinecraftAssets] and [addMappedMinecraft]
 */
fun Project.pullDeps(version: MinecraftVersion, mappings: MinecraftMappings) {
    addMinecraftAssets(version)
    addMappedMinecraft(version, mappings)
}

/**
 * Adds Minecraft as a dependency by providing the jar to the projects file tree.
 */
private fun Project.addMinecraftAssets(version: MinecraftVersion) {
    val manifest = DownloadUtil.fetch(Constants.VERSION_MANIFEST).decodeJSON<VersionManifest>() ?: return
    val versionEntry = manifest.versions.find { it.id == version.id } ?: return
    val versionInfo = DownloadUtil.fetch(versionEntry.url).decodeJSON<VersionInfo>() ?: return

    val client = versionInfo.downloads.client
    DownloadUtil.checksumAndDownload(URL(client.url), client.sha1, version.minecraftJarCache.toPath())

    repositories.maven {
        name = "mojang"
        setUrl("https://libraries.minecraft.net/")
    }

    versionInfo.libraries.filter { "twitch-platform" !in it.name && "twitch-external" !in it.name }
        .forEach { dependencies.add("compileOnly", it.name) }

    addMojangMappings(version, versionInfo)
}

/**
 * Downloads Official Mojang mappings
 */
private fun addMojangMappings(version: MinecraftVersion, versionInfo: VersionInfo) {
    val mappings = versionInfo.downloads.mappings

    if (mappings.size != -1) {
        DownloadUtil.checksumAndDownload(
            URL(mappings.url),
            mappings.sha1,
            version.cacheDirectory.toPath().resolve("mojang.mappings")
        )
    }
}

private fun Project.addMappedMinecraft(version: MinecraftVersion, mappings: MinecraftMappings) = runCatching {
    val mapped = File(version.cacheDirectory, "client-${mappings.id}.jar")
    if (!mapped.exists()) {
        val fullMappings = MappingsLoader.loadMappings(mappings.mappingsStream(version).toLines())
        remapJar(fullMappings, version.minecraftJarCache, mapped)
    }

    dependencies.add("compileOnly", project.files(mapped))
}.onFailure { it.printStackTrace() }

fun InputStream.toLines(): List<String> =
    this.readBytes().decodeToString().trim().lines()

@Serializable
private data class VersionManifest(val versions: List<ManifestVersion>)

@Serializable
private data class ManifestVersion(val id: String, val url: String)

@Serializable
private data class VersionInfo(val downloads: VersionDownloads, val libraries: List<Library>)

@Serializable
private data class VersionDownloads(
    val client: VersionDownload,

    @SerialName("client_mappings")
    val mappings: ClientMappings = ClientMappings("", "", -1) // not all versions will have a client_mappings url
)

@Serializable
private data class ClientMappings(
    val url: String,
    val sha1: String,
    val size: Int = -1
)

@Serializable
private data class VersionDownload(val url: String, val sha1: String)

@Serializable
private data class Library(val name: String)
