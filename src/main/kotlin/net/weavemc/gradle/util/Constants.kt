package net.weavemc.gradle.util

import kotlinx.serialization.json.Json
import java.io.File

/**
 * A class containing constant mnemonic values to
 * be referenced throughout the project.
 */
object Constants {
    /**
     * The gradle cache directory.
     *
     *  *  Windows: `"%USERPROFILE%\.gradle\caches\weave\"`
     *  *  Linux:   `"${HOME}/.gradle/caches/weave/"`
     *  *  Mac:     `"${HOME}/.gradle/caches/weave/"`
     */
    val CACHE_DIR = File(System.getProperty("user.home"), ".gradle/caches/weave")

    /**
     * The global JSON serializer
     */
    val JSON = Json { ignoreUnknownKeys = true }

    /**
     * The version manifest URL
     */
    const val VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json"
}
