package club.maxstats.weave.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    public static String download(String url, String destinationPath) {
        try {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            String filePath = destinationPath + '/' + fileName;

            try (InputStream in = new URL(url).openStream()) {
                Files.createDirectories(Paths.get(destinationPath));
                Files.copy(in, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
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
     * @param url The URL to download from.
     * @param checksum The checksum to compare to.
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

    public static List<String> downloadUnzipped(String url, String destinationPath) {
        try (ZipInputStream zipIn = new ZipInputStream(new URL(url).openStream())) {
            List<String> paths = new ArrayList<>();

            ZipEntry entry = null;
            while((entry = zipIn.getNextEntry()) != null) {
                try (ByteArrayOutputStream entryContentStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int len;

                    while ((len = zipIn.read(buffer)) > 0)
                        entryContentStream.write(buffer, 0, len);

                    byte[] content = entryContentStream.toByteArray();

                    /* Prevents directory traversal attacks.
                     * CWE-22: Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal') */
                    if (entry.getName().contains(".."))
                        continue;

                    Path path = Paths.get(destinationPath + '/' + entry.getName());
                    Files.createDirectories(path.getParent());
                    Files.write(path, content);
                    paths.add(path.toString());
                }
            }

            return paths;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String downloadEntryFromZip(String url, String entryName, String destinationPath) {
        try (ZipInputStream zipIn = new ZipInputStream(new URL(url).openStream())) {
            ZipEntry entry;
            while((entry = zipIn.getNextEntry()) != null) {
                if (entry.getName().equals(entryName)) {
                    try (ByteArrayOutputStream entryContentStream = new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[4096];
                        int len;

                        while ((len = zipIn.read(buffer)) > 0)
                            entryContentStream.write(buffer, 0, len);

                        byte[] content = entryContentStream.toByteArray();

                        Path filePath = Paths.get(destinationPath + '/' + entryName);
                        Files.createDirectories(filePath.getParent());
                        Files.write(filePath, content);
                        return filePath.toString();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Downloads and replaces in async.
     *
     * @param urls The URLs to download from.
     * @param destinationPath The path/directory to download to.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String[] downloadMultipleAsync(String[] urls, String destinationPath) {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(urls.length);

            List<Future<String>> filePaths = new ArrayList<>();
            for (String url : urls) {
                filePaths.add(pool.submit(new DownloadTask(url, destinationPath)));
            }

            pool.shutdown();
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

            return getPaths(filePaths);
        } catch (InterruptedException ignored) {
        }

        return new String[0];
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String[] downloadAndChecksumMultipleAsync(Map<String, String> urlChecksumMap, String destinationPath) {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(urlChecksumMap.size());

            List<Future<String>> filePaths = new ArrayList<>();
            for (Map.Entry<String, String> entry : urlChecksumMap.entrySet()) {
                filePaths.add(pool.submit(new DownloadTask(entry.getKey(), entry.getValue(), destinationPath)));
            }

            pool.shutdown();
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

            return getPaths(filePaths);
        } catch (InterruptedException ignored) {
        }

        return new String[0];
    }

    private static String[] getPaths(List<Future<String>> filePaths) throws InterruptedException {
        return filePaths.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).toArray(String[]::new);
    }

    private static class DownloadTask implements Callable<String> {
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
        public String call() {
            try {
                if (this.checksum == null)
                    return download(this.url, this.destinationPath);
                else
                    return downloadAndChecksum(this.url, this.checksum, this.destinationPath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }
    }

}
