package club.maxstats.weave;

import club.maxstats.weave.configuration.DependencyManager;
import club.maxstats.weave.configuration.WeaveMinecraftExtension;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Gradle build system plugin used to automate the setup of a modding environment.
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
        /* Applying our default plugins. */
        project.getPluginManager().apply(JavaPlugin.class);
        project.getRepositories().mavenCentral();
        project.getRepositories().mavenLocal();

        WeaveMinecraftExtension ext = project.getExtensions().create("minecraft", WeaveMinecraftExtension.class);

        project.afterEvaluate(proj -> {
            proj.getTasks().named("classes", task -> {
                task.doLast(action -> {
                    SourceSet main       = proj.getExtensions().getByType(SourceSetContainer.class).getByName(SourceSet.MAIN_SOURCE_SET_NAME);
                    FileTree  classesDir = main.getOutput().getClassesDirs().getAsFileTree().matching(patternFilterable -> patternFilterable.include("**/*.class"));

                    JsonObject json = new JsonObject();
                    JsonArray hooksArray = new JsonArray();
                    JsonArray mixinsArray = new JsonArray();

                    classesDir.getFiles().forEach(file -> {
                        try {
                            ClassReader reader = new ClassReader(new FileInputStream(file));

                            if (reader.getSuperName().equals("club/maxstats/weave/loader/api/Hook")) {
                                hooksArray.add(reader.getClassName());
                            }

                            ClassNode node = new ClassNode();
                            reader.accept(node, 0);

                            if (node.visibleAnnotations != null) {
                                if (
                                    node.visibleAnnotations
                                    .stream()
                                    .anyMatch(annotationNode -> annotationNode.desc.equals("Lclub/maxstats/weave/loader/api/mixin/Mixin;"))
                                ) {
                                    mixinsArray.add(node.name);
                                }
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException("Failed to build configuration file", ex);
                        }
                    });

                    json.add("Hooks", hooksArray);
                    json.add("Mixins", mixinsArray);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonOutput = gson.toJson(json);

                    try (FileWriter writer = new FileWriter(main.getOutput().getResourcesDir().getPath() + "/weavin.conf")) {
                        writer.write(jsonOutput);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            });

            new DependencyManager(proj, ext.getVersion().get()).pullDeps();
        });
    }

}
