package club.maxstats.weave.util;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class Utils {

    /**
     * Returns the Minecraft jar file.
     * @return The Minecraft jar file.
     */
    public File getMinecraftJar(String version) {
        return new File(Constants.CACHE_DIR + "/" + version, "client.jar");
    }

}
