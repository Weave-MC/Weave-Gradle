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

    public static JsonObject getJsonFromURL(String url) {
        try {
            return JsonParser.parseReader(new InputStreamReader(new URL(url).openConnection().getInputStream())).getAsJsonObject();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

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
        } catch (NoSuchAlgorithmException ignored) {
        }

        return "";
    }

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

    public static void downloadMultipleAsync(String[] urls, String destinationPath) {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(urls.length);

            for (String url : urls) {
                executor.submit(new DownloadTask(url, destinationPath));
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    public static void downloadAndChecksumMultipleAsync(Map<String, String> urlChecksumMap, String destinationPath) {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(urlChecksumMap.size());

            for (Map.Entry<String, String> entry : urlChecksumMap.entrySet()) {
                executor.submit(new DownloadTask(entry.getKey(), entry.getValue(), destinationPath));
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    private static class DownloadTask implements Runnable {
        private String url;
        private String destinationPath;
        private String checksum = null;

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
