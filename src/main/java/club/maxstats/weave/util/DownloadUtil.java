package club.maxstats.weave.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.var;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DownloadUtil {

    /**
     * Grabs a {@link JsonObject} from the inputted argument.
     *
     * @param url The URL to fetch our JSON from.
     * @return our {@link JsonObject} parsed through {@link InputStreamReader}.
     */
    public static JsonObject getJsonFromURL(String url) {
        try (var stream = new URL(url).openStream()) {
            return JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the SHA1 checksum of the file as a {@link String}.
     *
     * @param filePath The path to the file.
     * @return the SHA1 checksum of the file.
     */
    private static String checksum(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) return "";

            MessageDigest digest = MessageDigest.getInstance("SHA1");

            try (InputStream is = Files.newInputStream(file.toPath())) {
                byte[] buffer    = new byte[4096];
                int    bytesRead = is.read(buffer);

                while (bytesRead >= 0) {
                    digest.update(buffer, 0, bytesRead);
                    bytesRead = is.read(buffer);
                }
            }

            byte[]        checksum = digest.digest();
            StringBuilder hex      = new StringBuilder();

            for (byte b : checksum) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ignored) {
        }

        return "";
    }

    /**
     * Downloads a file from any URL to the user specified directory.
     *
     * @param url             The URL to download from.
     * @param destinationPath The path/directory to download to.
     */
    public static String download(String url, String destinationPath) {
        try {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            String filePath = destinationPath + '/' + fileName;

            try (InputStream is = new URL(url).openStream()) {
                Files.createDirectories(Paths.get(destinationPath));
                Files.copy(is, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                return filePath;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Downloads and checksums the file.
     *
     * @param url             The URL to download from.
     * @param checksum        The checksum to compare to.
     * @param destinationPath The path/directory to download to.
     */
    public static String downloadAndChecksum(String url, String checksum, String destinationPath) {
        try {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            String filePath = destinationPath + '/' + fileName;

            if (!checksum(filePath).equals(checksum)) {
                try (InputStream in = new URL(url).openStream()) {
                    Files.createDirectories(Paths.get(destinationPath));
                    Files.copy(in, Paths.get(destinationPath + '/' + fileName), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            return filePath;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
