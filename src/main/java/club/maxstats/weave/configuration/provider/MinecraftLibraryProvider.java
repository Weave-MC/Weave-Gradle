package club.maxstats.weave.configuration.provider;

import club.maxstats.weave.util.DownloadUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinecraftLibraryProvider {
    private MinecraftProvider minecraftProvider;
    public MinecraftLibraryProvider(MinecraftProvider minecraftProvider) {
        this.minecraftProvider = minecraftProvider;
    }

    public void provide() {
        JsonArray librariesArray = this.minecraftProvider.getVersionJson().get("libraries").getAsJsonArray();

        Map<String, String> urlChecksumMap = new HashMap<>();
        for (int i = 0; i < librariesArray.size(); i++) {
            JsonObject library = librariesArray.get(i).getAsJsonObject();
            JsonObject libraryArtifact = library.getAsJsonObject("downloads").getAsJsonObject("artifact");

            /* If library artifact is null, entry is a natives jar which is not needed */
            if (libraryArtifact == null) continue;

            String url = libraryArtifact.get("url").getAsString();
            String checksum = libraryArtifact.get("sha1").getAsString();

            urlChecksumMap.put(url, checksum);
        }

        DownloadUtil.downloadAndChecksumMultipleAsync(urlChecksumMap, this.minecraftProvider.getDownloadPath() + "/libraries");
    }
}
