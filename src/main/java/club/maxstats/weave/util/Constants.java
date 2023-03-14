package club.maxstats.weave.util;

import java.io.File;

/**
 * A class containing constant mnemonic values to
 * be referenced throughout the project.
 */
public class Constants {

    /**
     * The gradle cache directory.
     *
     * <li> Windows: {@code "%USERPROFILE%\.gradle\caches\weave\"}
     * <li> Linux:   {@code "${HOME}/.gradle/caches/weave/"}
     * <li> Mac:     {@code "${HOME}/.gradle/caches/weave/"}
     */
    public static final File CACHE_DIR = new File(System.getProperty("user.home"), ".gradle/caches/weave");

}
