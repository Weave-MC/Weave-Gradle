package club.maxstats.weave.util;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class Utils {

    /**
     * @return The Minecraft jar file.
     */
    public File getMinecraftJar(String version) {
        return new File(Constants.CACHE_DIR + "/" + version, "client.jar");
    }

}
