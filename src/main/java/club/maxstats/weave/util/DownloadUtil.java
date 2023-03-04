package club.maxstats.weave.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadUtil {

    /**
     * Grabs a {@link JsonObject} from the inputted argument.
     *
     * @param url The URL to fetch our JSON from.
     * @return our {@link JsonObject} parsed through {@link InputStreamReader}.
     */
    public static JsonObject getJsonFromURL(String url) {
        try {
            return JsonParser.parseReader(new InputStreamReader(new URL(url).openConnection().getInputStream())).getAsJsonObject();
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
            if (!file.exists())
                return "";

            MessageDigest digest = MessageDigest.getInstance("SHA1");

            try (InputStream is = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            byte[] checksum = digest.digest();
            StringBuilder hex = new StringBuilder();

            for (byte b : checksum) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ignored) {}

        return "";
    }

    /**
     * Downloads a file from any URL to the user specified directory.
     *
     * @param url The URL to download from.
     * @param destinationPath The path/directory to download to.
     */
    public static void download(String url, String destinationPath) {
        try {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            Files.createDirectories(Paths.get(destinationPath));

            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, Paths.get(destinationPath + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Downloads and checksums the file.
     *
     * @param url The URL to download from.
     * @param checksum The checksum to compare to.
     * @param destinationPath The path/directory to download to.
     */
    public static void downloadAndChecksum(String url, String checksum, String destinationPath) {
        try {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            String filePath = destinationPath + "/" + fileName;

            if (checksum(filePath).equals(checksum)) {
                return;
            }

            Files.createDirectories(Paths.get(destinationPath));
            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, Paths.get(destinationPath + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Downloads and replaces in async.
     *
     * @param urls The URLs to download from.
     * @param destinationPath The path/directory to download to.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void downloadMultipleAsync(String[] urls, String destinationPath) {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(urls.length);

            for (String url : urls) {
                pool.submit(new DownloadTask(url, destinationPath));
            }

            pool.shutdown();
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void downloadAndChecksumMultipleAsync(Map<String, String> urlChecksumMap, String destinationPath) {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(urlChecksumMap.size());

            for (Map.Entry<String, String> entry : urlChecksumMap.entrySet()) {
                pool.submit(new DownloadTask(entry.getKey(), entry.getValue(), destinationPath));
            }

            pool.shutdown();
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    private static class DownloadTask implements Runnable {
        private final String url;
        private final String destinationPath;
        private       String checksum = null;

        public DownloadTask(String url, String destinationPath) {
            this.url = url;
            this.destinationPath = destinationPath;
        }

        public DownloadTask(String url, String checksum, String destinationPath) {
            this.url = url;
            this.destinationPath = destinationPath;
            this.checksum = checksum;
        }

        @Override
        public void run() {
            try {
                if (this.checksum == null)
                    download(this.url, this.destinationPath);
                else
                    downloadAndChecksum(this.url, this.checksum, this.destinationPath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
