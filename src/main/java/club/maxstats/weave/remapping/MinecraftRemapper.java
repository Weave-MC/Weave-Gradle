package club.maxstats.weave.remapping;

import club.maxstats.weave.configuration.MinecraftVersion;
import lombok.experimental.UtilityClass;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class MinecraftRemapper {

    public Remapper create(MinecraftVersion version) throws IOException {
        try (InputStream mappings = version.getMappingStream()) {
            return new SimpleRemapper(parseMappings(mappings));
        }
    }

    /**
     * Parses the mappings from the specified stream.
     *
     * @param mappingsStream The stream from which to read the mappings
     * @return The mappings according to their corresponding version.
     */
    private Map<String, String> parseMappings(InputStream mappingsStream) throws IOException {
        Map<String, String> mappings = new HashMap<>();
        BufferedReader           rdr = new BufferedReader(new InputStreamReader(mappingsStream));

        while (rdr.ready()) {
            String line = rdr.readLine();
            if (line.isEmpty()) continue;

            String[] split = line.substring(4).split(" ");
            switch (line.substring(0, 4)) {
                case "CL: ": {
                    mappings.put(split[0], split[1]);
                    break;
                }
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
