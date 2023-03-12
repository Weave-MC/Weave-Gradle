package club.maxstats.weave.remapping;

import club.maxstats.weave.WeavePlugin;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.objectweb.asm.commons.SimpleRemapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        var mappings = new HashMap<String, String>();

        var stream = WeavePlugin.class.getResourceAsStream("/mappings/lunar_named_b2_" + version + ".xsrg");
        if (stream == null) {
            throw new RuntimeException("No mappings available for version " + version);
        }

        @Cleanup var rdr = new BufferedReader(new InputStreamReader(stream));
        while (rdr.ready()) {
            var line = rdr.readLine();
            if (line.isBlank()) continue;

            var split = line.substring(4).split(" ");
            switch (line.substring(0, 4)) {
                case "CL: " -> mappings.put(split[0], split[1]);
                case "MD: " -> {
                    var i      = split[0].lastIndexOf('/');
                    var clazz  = split[0].substring(0, i);
                    var method = split[0].substring(i + 1);

                    mappings.put(String.format("%s.%s%s", clazz, method, split[1]), split[2].substring(split[2].lastIndexOf('/') + 1));
                }
                case "FD: " -> {
                    var i     = split[0].lastIndexOf('/');
                    var clazz = split[0].substring(0, i);
                    var field = split[0].substring(i + 1);

                    mappings.put(String.format("%s.%s", clazz, field), split[2].substring(split[2].lastIndexOf('/') + 1));
                }
            }
        }
        return mappings;
    }

}
