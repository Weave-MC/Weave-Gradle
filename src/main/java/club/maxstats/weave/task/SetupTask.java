package club.maxstats.weave.task;

import club.maxstats.weave.remapping.NotchToMCPRemapper;
import club.maxstats.weave.util.Constants;
import club.maxstats.weave.util.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
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

/**
 * TODO: Add description here.
 * @author Scherso (<a href="https://github.com/Scherso">...</a>), Max (<a href="https://github.com/exejar">...</a>)
 */
public class SetupTask extends DefaultTask {

    public SetupTask() {
        super();
    }

    /**
     * Sets up the decompiled workspace for the user in their development environment.
     */
    @TaskAction
    public void setupDecompWorkspace() {
        Project project = this.getProject();

        try {
            JarFile mcJar = new JarFile(Utils.getMinecraftJar());
            Enumeration<? extends JarEntry> entries = mcJar.entries();

            File outputDir = new File(Constants.CACHE_DIR, "1.8.9-mapped.jar");
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

                Remapper remapper = new NotchToMCPRemapper();
                ClassRemapper classRemapper = new ClassRemapper(cw, remapper);
                cr.accept(classRemapper, 0);

                String mappedName = remapper.map(cr.getClassName());
                byte[] bytes = cw.toByteArray();

                JarEntry newEntry = new JarEntry(mappedName);
                newEntry.setSize(bytes.length);
                jos.putNextEntry(newEntry);
                jos.write(bytes);
                jos.closeEntry();
            }

            jos.close();
            project.getDependencies().add("compileOnly", project.files(".gradle/minecraft/1.8.9-mapped.jar"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
