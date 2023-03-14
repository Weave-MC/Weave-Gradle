package club.maxstats.weave.configuration.provider;

import club.maxstats.weave.configuration.MinecraftVersion;
import club.maxstats.weave.util.DownloadUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.gradle.api.Project;

@Getter
@RequiredArgsConstructor
public class MinecraftProvider {

    private final Project          project;
    private final MinecraftVersion version;
    private       JsonObject       versionJson;

    public void provide() {
        JsonObject manifestJson = DownloadUtil.getJsonFromURL("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json");

        /* Null check on manifestJson. */
        if (manifestJson == null)  return;

        JsonArray  versionArray  = manifestJson.getAsJsonArray("versions");
        JsonObject versionObject = null;

        for (int i = 0; i < versionArray.size(); i++) {
            JsonObject version = versionArray.get(i).getAsJsonObject();
            if (version.get("id").getAsString().equals(this.version.getId())) {
                versionObject = version;
                break;
            }
        }

        /* Null check on versionObject. */
        if (versionObject == null) return;
        JsonObject versionJson = DownloadUtil.getJsonFromURL(versionObject.get("url").getAsString());
        /* Yet another null check. */
        if (versionJson == null)   return;

        this.versionJson = versionJson;

        JsonObject downloadsObject = versionJson.getAsJsonObject("downloads");
        JsonObject clientObject    = downloadsObject.getAsJsonObject("client");
        String     clientURL       = clientObject.get("url").getAsString();
        String     checksum        = clientObject.get("sha1").getAsString();

        DownloadUtil.downloadAndChecksum(clientURL, checksum, this.version.getCacheDirectory().getPath());
        new MinecraftLibraryProvider(this).provide();
    }

}
