package club.maxstats.weave.configuration;

import club.maxstats.weave.configuration.provider.MappingsProvider;
import club.maxstats.weave.configuration.provider.MinecraftProvider;
import club.maxstats.weave.remapping.NotchToMCPRemapper;
import club.maxstats.weave.util.Constants;
import club.maxstats.weave.util.Utils;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class DependencyManager {

    private final String version;
    private final Project project;

    public DependencyManager(Project project) {
        this.project = project;

        WeaveMinecraftExtension ext = WeaveMinecraftExtension.get(project);
        this.version = ext.getVersion();
    }

    /**
     * Pulls dependencies from {@link #addMinecraftAssets()} 
     * and {@link #addMappedMinecraft()}.
     */
    public void pullDeps() {
        if (this.version.isEmpty())
            return;

        this.addMinecraftAssets();
        this.addMappedMinecraft();
    }

    /**
     * Adds Minecraft as a dependency by providing the jar to the
     * projects file tree. 
     */
    public void addMinecraftAssets() {
        new MinecraftProvider(this.project, this.version).provide();
        new MappingsProvider(this.project, this.version).provide();

        String versionPath = Constants.CACHE_DIR + "/" + this.version + "/libraries";

        this.project.getDependencies().add("compileOnly", this.project.fileTree(versionPath).include("*.jar"));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void addMappedMinecraft() {
        try {
            String versionPath = Constants.CACHE_DIR + "/" + this.version;

            JarFile mcJar   = new JarFile(Utils.getMinecraftJar(this.version));
            Enumeration<? extends JarEntry> entries = mcJar.entries();

            File output = new File(versionPath, "minecraft-mapped.jar");
            if (!output.exists() /* TODO create checksums for each mapped jar and compare to the jar file */) {
                output.createNewFile();
                JarOutputStream jos = new JarOutputStream(new FileOutputStream(output));

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    
                    if (!entry.getName().endsWith(".class")) {
                        continue;
                    }

                    ClassReader cr = new ClassReader(mcJar.getInputStream(entry));
                    ClassWriter cw = new ClassWriter(0);

                    Remapper remapper           = new NotchToMCPRemapper();
                    ClassRemapper classRemapper = new ClassRemapper(cw, remapper);
                    cr.accept(classRemapper, 0);

                    String mappedName = remapper.map(cr.getClassName());
                    byte[] bytes = cw.toByteArray();

                    JarEntry newEntry = new JarEntry(mappedName + ".class");
                    newEntry.setSize(bytes.length);
                    jos.putNextEntry(newEntry);
                    jos.write(bytes);
                    jos.closeEntry();
                }

                jos.close();
            }

            this.project.getDependencies().add("compileOnly", this.project.fileTree(versionPath).include("minecraft-mapped.jar"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
