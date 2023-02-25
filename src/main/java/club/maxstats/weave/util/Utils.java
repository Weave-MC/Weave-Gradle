package club.maxstats.weave.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;

@UtilityClass
public class Utils {

    private String getLocalMinecraftPath() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return System.getenv("appdata") + "/.minecraft";
        } else if (SystemUtils.IS_OS_MAC) {
            return System.getProperty("user.home") +  "/Library/Application Support/minecraft";
        } else {
            return System.getProperty("user.home") + "/.minecraft";
        }
    }

    public File getMinecraftJar() {
        return new File(getLocalMinecraftPath(), "versions/1.8.9/1.8.9.jar");
    }
}
