package org.cubewhy.utils;

import java.io.InputStream;

public class FileUtils {
    public static InputStream getFile(String pathToFile) {
        return FileUtils.class.getResourceAsStream(pathToFile);
    }
}
