package club.maxstats.weave.configuration

import org.objectweb.asm.commons.SimpleRemapper
import java.io.InputStream

fun createMinecraftRemapper(version: MinecraftVersion) =
    version.mappingStream.use { SimpleRemapper(it.parseMappings()) }

/**
 * Parses the mappings from the specified stream.
 *
 * @receiver The stream from which to read the mappings
 * @return The mappings according to their corresponding version.
 */
private fun InputStream.parseMappings() = bufferedReader().useLines { seq ->
    seq.filter { it.isNotEmpty() }.associate { line ->
        val split = line.drop(4).split(" ")
        when (line.take(4)) {
            "CL: " -> split[0] to split[1]

            "MD: " -> {
                val clazz  = split[0].substringBeforeLast('/')
                val method = split[0].substringAfterLast('/')

                "$clazz.$method${split[1]}" to split[2].substringAfterLast('/')
            }

            "FD: " -> {
                val clazz = split[0].substringBeforeLast('/')
                val field = split[0].substringAfterLast('/')

                "$clazz.$field" to split[2].substringAfterLast('/')
            }

            else -> error("Invalid mapping sequence")
        }
    }
}
