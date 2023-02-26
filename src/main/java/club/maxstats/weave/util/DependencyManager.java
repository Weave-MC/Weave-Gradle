package club.maxstats.weave.util;

import club.maxstats.weave.remapping.NotchToMCPRemapper;
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
    private final Project project;
    public DependencyManager(Project project) {
        this.project = project;
    }

    public void pullDeps() {
        /* Hi */
        this.addMinecraftAssets();
        this.addMappedMinecraft();
    }

    public void addMinecraftAssets() {

    }

    public void addMappedMinecraft() {
        try {
            JarFile mcJar   = new JarFile(Utils.getMinecraftJar());
            Enumeration<? extends JarEntry> entries = mcJar.entries();

            File outputDir = new File(Constants.MC_CACHE_DIR, "minecraft-mapped.jar");
            if (!outputDir.exists()) {
                outputDir.getParentFile().mkdirs();
                outputDir.createNewFile();
            }

            JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputDir));

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().endsWith(".class")) {
                    continue;
                }

                ClassReader cr = new ClassReader(mcJar.getInputStream(entry));
                ClassWriter cw = new ClassWriter(0);

                Remapper remapper      = new NotchToMCPRemapper();
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

            this.project.getDependencies().add("compileOnly", this.project.fileTree(Constants.MC_CACHE_DIR).include("*.jar"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
