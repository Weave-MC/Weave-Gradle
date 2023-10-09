package net.weavemc.gradle.configuration

import net.weavemc.gradle.mapping.LambdaAwareRemapper
import net.weavemc.gradle.mapping.Mappings
import net.weavemc.gradle.mapping.MappingsRemapper
import org.objectweb.asm.*
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

fun remapJar(
    mappings: Mappings,
    input: File,
    output: File,
    from: String = "official",
    to: String = "named",
    classpath: List<File> = listOf(),
) {
    val cache = hashMapOf<String, ByteArray?>()
    val jarsToUse = (classpath + input).map { JarFile(it) }
    val lookup = jarsToUse.flatMap { j ->
        j.entries().asSequence().filter { it.name.endsWith(".class") }
            .map { it.name.dropLast(6) to { j.getInputStream(it).readBytes() } }
    }.toMap()

    JarFile(input).use { jar ->
        JarOutputStream(output.outputStream()).use { out ->
            val (classes, resources) = jar.entries().asSequence().partition { it.name.endsWith(".class") }

            fun write(name: String, bytes: ByteArray) {
                out.putNextEntry(JarEntry(name))
                out.write(bytes)
            }

            resources.filterNot { it.name.endsWith(".RSA") || it.name.endsWith(".SF") }
                .forEach { write(it.name, jar.getInputStream(it).readBytes()) }

            val remapper = MappingsRemapper(
                mappings, from, to,
                loader = { name -> if (name in lookup) cache.getOrPut(name) { lookup.getValue(name)() } else null }
            )

            classes.forEach { entry ->
                val reader = ClassReader(jar.getInputStream(entry).readBytes())
                val writer = ClassWriter(reader, 0)

                reader.accept(LambdaAwareRemapper(writer, remapper), 0)

                write("${remapper.map(reader.className)}.class", writer.toByteArray())
            }
        }
    }

    jarsToUse.forEach { it.close() }
}
