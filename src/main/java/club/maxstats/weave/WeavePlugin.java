package club.maxstats.weave;

import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

public class WeavePlugin implements Plugin<Project> {

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    @Override
    public void apply(@NonNull Project target) {
        target.getPluginManager().apply(JavaPlugin.class);
    }

}
