package net.weavemc.gradle.configuration

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import net.weavemc.gradle.util.AccessWidener
import net.weavemc.gradle.util.Constants
import net.weavemc.gradle.util.DownloadUtil
import org.gradle.api.Project
import org.gradle.kotlin.dsl.maven
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import java.io.File
import java.net.URL
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

private inline fun <reified T> String?.decodeJSON() =
    if (this != null) Constants.JSON.decodeFromString<T>(this) else null

/**
 * Pulls dependencies from [addMinecraftAssets] and [addMappedMinecraft]
 */
fun Project.pullDeps(version: MinecraftVersion) {
    addMinecraftAssets(version)
    addMappedMinecraft(version)
}

/**
 * Adds Minecraft as a dependency by providing the jar to the projects file tree.
 */
private fun Project.addMinecraftAssets(version: MinecraftVersion) {
    val manifest = DownloadUtil.fetch(Constants.VERSION_MANIFEST).decodeJSON<VersionManifest>() ?: return
    val versionEntry = manifest.versions.find { it.id == version.id } ?: return
    val versionInfo = DownloadUtil.fetch(versionEntry.url).decodeJSON<VersionInfo>() ?: return

    val client = versionInfo.downloads.client
    DownloadUtil.downloadAndChecksum(URL(client.url), client.sha1, version.minecraftJarCache.toPath())

    repositories.maven("https://libraries.minecraft.net/")

    versionInfo.libraries.filter { "twitch-platform" !in it.name && "twitch-external" !in it.name }
        .forEach { dependencies.add("compileOnly", it.name) }
}

/**
 * Adds the mapped Minecraft JAR to the project's dependencies.
 *
 * @param version The Minecraft version for which to add the mapped JAR.
 */
private fun Project.addMappedMinecraft(version: MinecraftVersion) = runCatching {
    val mapped = File(version.cacheDirectory, "minecraft-mapped.jar")
    if (mapped.exists()) {
        dependencies.add("compileOnly", files(mapped))
        return@runCatching
    }

    val remapper = createMinecraftRemapper(version)

    JarFile(version.minecraftJarCache).use { mcJar ->
        JarOutputStream(mapped.outputStream()).use { outputStream ->
            for (entry in mcJar.entries()) {
                if (!entry.name.endsWith(".class")) continue

                val reader = ClassReader(mcJar.getInputStream(entry))
                val cw = ClassWriter(0)
                reader.accept(AccessWidener(ClassRemapper(cw, remapper)), 0)

                val mappedName = remapper.map(reader.className) ?: reader.className
                outputStream.putNextEntry(JarEntry("$mappedName.class"))
                outputStream.write(cw.toByteArray())
            }
        }
    }

    dependencies.add("compileOnly", files(mapped))
}.onFailure { it.printStackTrace() }

/**
 * Represents a version manifest containing a list of manifest versions
 *
 * @property versions The list of manifest versions
 */
@Serializable
private data class VersionManifest(val versions: List<ManifestVersion>)

/**
 * Represents a manifest version with an ID and URL.
 *
 * @property id The ID of the manifest version.
 * @property url The URL of the manifest version.
 */
@Serializable
private data class ManifestVersion(val id: String, val url: String)

/**
 * Represents version information containing downloads and a list of libraries.
 *
 * @property downloads The version downloads.
 * @property libraries The list of libraries.
 */
@Serializable
private data class VersionInfo(val downloads: VersionDownloads, val libraries: List<Library>)

/**
 * Represents version downloads containing the client download.
 *
 * @property client The client download.
 */
@Serializable
private data class VersionDownloads(val client: VersionDownload)

/**
 * Represents a version download with a URL and SHA1 hash.
 *
 * @property url The URL of the version download.
 * @property sha1 The SHA1 hash of the version download.
 */
@Serializable
private data class VersionDownload(val url: String, val sha1: String)

/**
 * Represents a library with a name.
 *
 * @property name The name of the library.
 */
@Serializable
private data class Library(val name: String)
