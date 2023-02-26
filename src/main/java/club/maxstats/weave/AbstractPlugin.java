package club.maxstats.weave;

import club.maxstats.weave.remapping.NotchToMCPRemapper;
import club.maxstats.weave.util.Constants;
import club.maxstats.weave.util.Utils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.internal.impldep.com.google.common.collect.ImmutableMap;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;
import org.gradle.plugins.ide.idea.model.IdeaModel;
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

public class AbstractPlugin implements Plugin<Project> {

    protected Project project;

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void apply(Project target) {
        project = target;

        /* Applying a Java-lang Gradle plugin. */
        target.getPluginManager().apply(JavaPlugin.class);


        configureIDE();

        project.afterEvaluate(project1 -> {
            try {
                JarFile                         mcJar   = new JarFile(Utils.getMinecraftJar());
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

                    Remapper      remapper      = new NotchToMCPRemapper();
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

    /**
     * Creates a task of the specified type.
     *
     * @param target The project to create the task in.
     * @param name   The name of the task.
     * @param type   The type of the task.
     * @return       The created task.
     * @param <T>    Inherited from {@link Task}.
     */
    public <T extends Task> T makeTask(Project target, String name, Class<T> type) {
        return target.getTasks().create(name, type);
    }

    /**
     * Permit to create a Task instance of the type in the project.
     *
     * @param name The name of the task.
     * @param type The type of the task instance.
     * @return     The created task object for the {@link #project}.
     * @param <T>  Inherited from {@link Task}.
     */
    public <T extends Task> T makeTask(String name, Class<T> type) {
        return makeTask(project, name, type);
    }

    protected void configureIDE() {
        /* For IntelliJ IDEA. */
//      IdeaModel ideaModel = project.getExtensions().getByType(IdeaModel.class);
        IdeaModel ideaModel = (IdeaModel) project.getExtensions().getByName("idea");
        ideaModel.getModule().getExcludeDirs().addAll(project.files(".gradle", "build", ".idea", "out").getFiles());
        ideaModel.getModule().setDownloadJavadoc(true);
        ideaModel.getModule().setDownloadSources(true);
        ideaModel.getModule().setInheritOutputDirs(true);
//      This is where we'd ad the Minecraft dependency configuration.
        ideaModel.getModule().getScopes().get("COMPILE").get("plus").add(project.getConfigurations().getByName("MC_DEPENDENCIES"));

        /* For Eclipse. */
//      EclipseModel eclipseModel = project.getExtensions().getByType(EclipseModel.class);
        EclipseModel eclipseModel = (EclipseModel) project.getExtensions().getByName("eclipse");
//      This is where we'd ad the Minecraft dependency configuration.
        eclipseModel.getClasspath().getPlusConfigurations().add(project.getConfigurations().getByName("MC_DEPENDENCIES"));
    }

    protected void configureCompilation() {

    }

}
