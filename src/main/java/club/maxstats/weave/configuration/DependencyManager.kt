package club.maxstats.weave.configuration

import club.maxstats.weave.util.Constants
import club.maxstats.weave.util.DownloadUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import java.io.File
import java.net.URL
import java.nio.file.Files
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
    DownloadUtil.downloadAndChecksum(URL(client.url), client.sha1, version.cacheDirectory.toPath())

    repositories.maven {
        name = "mojang"
        setUrl("https://libraries.minecraft.net/")
    }

    versionInfo.libraries.filter { "twitch-platform" !in it.name && "twitch-external" !in it.name }
        .forEach { dependencies.add("compileOnly", it.name) }
}

private fun Project.addMappedMinecraft(version: MinecraftVersion) = runCatching {
    val mcJar = JarFile(version.minecraftJarCache)
    val entries = mcJar.entries()
    val mapped = File(version.cacheDirectory, "minecraft-mapped.jar")

    // TODO: create checksums for each mapped jar and compare to the jar file
    if (!mapped.exists()) {
        val output = JarOutputStream(Files.newOutputStream(mapped.toPath()))
        val remapper = createMinecraftRemapper(version)

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (!entry.name.endsWith(".class")) continue

            val reader = ClassReader(mcJar.getInputStream(entry))
            val writer = ClassWriter(0)

            reader.accept(ClassRemapper(writer, remapper), 0)

            val mappedName = remapper.map(reader.className) ?: reader.className
            val bytes = writer.toByteArray()

            val newEntry = JarEntry("$mappedName.class")
            output.putNextEntry(newEntry)
            output.write(bytes)
            output.closeEntry()
        }

        output.close()
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