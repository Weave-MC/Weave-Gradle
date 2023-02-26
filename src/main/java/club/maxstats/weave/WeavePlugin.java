package club.maxstats.weave;

import club.maxstats.weave.remapping.NotchToMCPRemapper;
import club.maxstats.weave.util.Constants;
import club.maxstats.weave.util.Utils;
import lombok.NonNull;
import org.gradle.api.Plugin;
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

/**
 * TODO: Add description here.
 *
 * @author Scherso (<a href="https://github.com/Scherso">...</a>), Max (<a href="https://github.com/exejar">...</a>)
 *         Nils <3 (<a href="https://github.com/Nilsen84">...</a>)
 * @version 1.0.0
 * @since 1.0.0
 */
public class WeavePlugin implements Plugin<Project> {

    /**
     * {@link Plugin#apply(Object)}
     *
     * @param project The target project.
     */
    @Override
    public void apply(@NonNull Project project) {
        project.afterEvaluate(project1 -> {
            try {
                JarFile mcJar = new JarFile(Utils.getMinecraftJar());
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

                    Remapper remapper = new NotchToMCPRemapper();
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

                project1.getDependencies().add("compileOnly", project1.fileTree(Constants.MC_CACHE_DIR).include("*.jar"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
