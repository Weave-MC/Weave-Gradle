package club.maxstats.weave.configuration.provider;

import club.maxstats.weave.remapping.Mappings;
import club.maxstats.weave.util.Constants;
import club.maxstats.weave.util.DownloadUtil;
import org.gradle.api.Project;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MappingsProvider {

    private final String baseMappingUrl = "https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_stable/{map_ver}-{mc_ver}/mcp_stable-{map_ver}-{mc_ver}.zip";
    private final String newBaseSrgUrl  = "https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_config/{mc_ver}/mcp_config-{mc_ver}.zip";
    private final String oldBaseSrgUrl  = "https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp/{mc_ver}/mcp-{mc_ver}-srg.zip";
    private String  version;
    private Project project;

    public MappingsProvider(Project project, String version) {
        this.project = project;
        this.version = version;
    }

    public void provide() {
        boolean isNew = this.version.compareTo("1.13") >= 0;
        if (this.version.contains("1.9") || this.version.contains("1.8") || this.version.contains("1.7")) isNew = false;

        String mapVer     = this.getMappingsVersion();
        String baseMapUrl = this.baseMappingUrl.replace("{map_ver}", mapVer).replace("{mc_ver}", this.version);

        String baseSrgUrl = isNew ? this.newBaseSrgUrl : this.oldBaseSrgUrl;
        baseSrgUrl = baseSrgUrl.replace("{mc_ver}", this.version);
        String srgFileName = isNew ? "config/joined.tsrg" : "joined.srg";

        String destinationPath = Constants.CACHE_DIR.getPath() + '/' + this.version + "/mappings";

        List<String> mapPaths      = DownloadUtil.downloadUnzipped(baseMapUrl, destinationPath);
        String       joinedMapPath = DownloadUtil.downloadEntryFromZip(baseSrgUrl, srgFileName, destinationPath);

        if (mapPaths == null || joinedMapPath == null) {
            System.err.println("Failed to download mappings for version " + this.version);
            System.exit(1);
        }

        String fieldsMapPath = null, methodsMapPath = null;
        for (String path : mapPaths) {
            if (path.endsWith("fields.csv")) {
                fieldsMapPath = path;
            } else if (path.endsWith("methods.csv")) {
                methodsMapPath = path;
            }
        }

        if (fieldsMapPath == null || methodsMapPath == null) {
            System.err.println("Failed to retrieve methods and fields mapping files");
            System.exit(1);
        }

        Mappings.createMappings(joinedMapPath, methodsMapPath, fieldsMapPath);
    }

    private String getMappingsVersion() {
        try (InputStream in = new URL("https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_stable/maven-metadata.xml").openStream()) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder        builder   = dbFactory.newDocumentBuilder();
            Document               document  = builder.parse(in);

            XPathFactory xpFactory  = XPathFactory.newInstance();
            XPath        xPath      = xpFactory.newXPath();
            String       expression = String.format("//version[substring-after(., '-') = '%s']", this.version);
            NodeList     versions   = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);

            System.out.println(versions.getLength());
            if (versions.getLength() > 0) {
                String version = versions.item(0).getTextContent();
                return version.substring(0, version.indexOf("-"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /* Default to 22 for 1.8.9 */
        return "22";
    }

}
