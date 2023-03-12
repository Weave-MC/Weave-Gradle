package club.maxstats.weave.configuration;

import club.maxstats.weave.configuration.provider.MinecraftProvider;
import club.maxstats.weave.remapping.MinecraftRemapper;
import club.maxstats.weave.util.Constants;
import club.maxstats.weave.util.Utils;
import lombok.AllArgsConstructor;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

@AllArgsConstructor
public class DependencyManager {

    private final Project project;
    private final String version;

    /**
     * Pulls dependencies from {@link #addMinecraftAssets()}
     * and {@link #addMappedMinecraft()}.
     */
    public void pullDeps() {
        this.addWeaveLoader();
        this.addMinecraftAssets();
        this.addMappedMinecraft();
    }

    private void addWeaveLoader() {
        this.project.getRepositories().maven(mavenArtifactRepository -> {
            mavenArtifactRepository.setName("jitpack");
            mavenArtifactRepository.setUrl("https://jitpack.io");
        });
        this.project.getDependencies().add("compileOnly", "com.github.weave-mc:weave-loader:0.1.0-alpha");
    }

    /**
     * Adds Minecraft as a dependency by providing the jar to the
     * projects file tree.
     */
    private void addMinecraftAssets() {
        new MinecraftProvider(this.project, this.version).provide();
    }

    private void addMappedMinecraft() {
        try {
            String versionPath = Constants.CACHE_DIR + "/" + this.version;

            JarFile mcJar = new JarFile(Utils.getMinecraftJar(this.version));
            Enumeration<? extends JarEntry> entries = mcJar.entries();

            File output = new File(versionPath, "minecraft-mapped.jar");
            if (!output.exists() /* TODO create checksums for each mapped jar and compare to the jar file */) {
                JarOutputStream jos = new JarOutputStream(new FileOutputStream(output));
                var remapper = new MinecraftRemapper(this.version);

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    if (!entry.getName().endsWith(".class")) {
                        continue;
                    }

                    ClassReader cr = new ClassReader(mcJar.getInputStream(entry));

                    ClassWriter cw = new ClassWriter(0);
                    cr.accept(new ClassRemapper(cw, remapper), 0);

                    String mappedName = Optional.ofNullable(
                        remapper.map(cr.getClassName())
                    ).orElse(cr.getClassName());

                    byte[] bytes = cw.toByteArray();

                    JarEntry newEntry = new JarEntry(mappedName + ".class");
                    jos.putNextEntry(newEntry);
                    jos.write(bytes);
                    jos.closeEntry();
                }

                jos.close();
            }

            this.project.getDependencies().add("compileOnly", project.files(output));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
