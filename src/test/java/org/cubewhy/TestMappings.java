package org.cubewhy;

import net.weavemc.gradle.configuration.MinecraftVersion;
import org.cubewhy.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestMappings {
    @Test
    public void testExists() throws Exception {
        for (MinecraftVersion version : MinecraftVersion.values()) {
            String path = "/mappings/" + version.getMappings();
            try {
                FileUtils.getFile(path); // throw exception when the mapping didn't exist
            } catch (Exception error) {
                throw new Exception("Mapping file " + path + " not found");
            }
        }
    }
}
