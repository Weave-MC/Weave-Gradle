package club.maxstats.weave.remapping;

import club.maxstats.weave.WeavePlugin;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.objectweb.asm.commons.SimpleRemapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MinecraftRemapper extends SimpleRemapper {

    public MinecraftRemapper(String version) {
        super(parseMappings(version));
    }

    /**
     * Parses the mappings from the specified version.
     *
     * @param version The version to fetch.
     * @return The mappings according to their corresponding version.
     */
    @SneakyThrows(IOException.class)
    private static Map<String, String> parseMappings(String version) {
        Map<String, String> mappings = new HashMap<>();

        InputStream stream = WeavePlugin.class.getResourceAsStream("/mappings/lunar_named_b2_" + version + ".xsrg");
        if (stream == null) {
            throw new RuntimeException("No mappings available for version " + version);
        }

        @Cleanup BufferedReader rdr = new BufferedReader(new InputStreamReader(stream));
        while (rdr.ready()) {
            String line = rdr.readLine();
            if (line.isEmpty()) continue;

            String[] split = line.substring(4).split(" ");
            switch (line.substring(0, 4)) {
                case "CL: ":
                    mappings.put(split[0], split[1]);
                    break;
                case "MD: ": {
                    int    i      = split[0].lastIndexOf('/');
                    String clazz  = split[0].substring(0, i);
                    String method = split[0].substring(i + 1);

                    mappings.put(String.format("%s.%s%s", clazz, method, split[1]), split[2].substring(split[2].lastIndexOf('/') + 1));
                    break;
                }
                case "FD: ": {
                    int    i     = split[0].lastIndexOf('/');
                    String clazz = split[0].substring(0, i);
                    String field = split[0].substring(i + 1);

                    mappings.put(String.format("%s.%s", clazz, field), split[2].substring(split[2].lastIndexOf('/') + 1));
                    break;
                }
            }
        }
        return mappings;
    }

}
