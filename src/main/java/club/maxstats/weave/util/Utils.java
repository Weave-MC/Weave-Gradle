package club.maxstats.weave.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;

@UtilityClass
public class Utils {

    /**
     * Returns the local Minecraft path.
     * @return The local Minecraft path.
     */
    private String getLocalMinecraftPath() {
        if (SystemUtils.IS_OS_MAC)
            return System.getenv("user.home") + "/Library/Application Support/minecraft";

        if (SystemUtils.IS_OS_LINUX)
            return System.getenv("user.home") + "/.minecraft";

        if (SystemUtils.IS_OS_WINDOWS)
            return System.getenv("appdata") + "/.minecraft";

        return null;
    }

    /**
     * Returns the Minecraft jar file.
     * @return The Minecraft jar file.
     */
    public File getMinecraftJar(String version) {
        return new File(Constants.CACHE_DIR + "/" + version, "client.jar");
    }

}
