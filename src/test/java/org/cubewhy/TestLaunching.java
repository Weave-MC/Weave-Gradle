package org.cubewhy;

import org.cubewhy.launcher.LunarClient;
import org.cubewhy.launcher.LunarDir;
import org.cubewhy.launcher.game.MinecraftArgs;
import org.cubewhy.lunarcn.JavaAgent;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestLaunching {
    @Test
    public void testLaunch() throws IOException {
        // Copy from https://github.com/cubewhy/LauncherLib/wiki/Client#join-args
        String version = "1.8.9"; // Minecraft version
        String module = "lunar"; // addon
        String branch = "master"; // LunarClient branch
        String baseDir = LunarDir.lunarDir + "/offline/multiver"; // workingDir | ClassPathDir
        MinecraftArgs mcArgs = new MinecraftArgs("%APPDATA%/.minecraft", LunarDir.lunarDir + "/textures", 300, 400);
        String java = "java.exe"; // Java exec
        String[] jvmArgs = new String[]{"-server"}; // Args for JVM
        String[] programArgs = new String[]{"--demo"}; // Args for game
        JavaAgent[] agents = new JavaAgent[]{}; // JavaAgents
        boolean setupNatives = true; // unzipNatives
        String args = LunarClient.getArgs(version, module, branch, baseDir, mcArgs, java, jvmArgs, programArgs, agents, setupNatives);
        System.out.println(args);
        Runtime.getRuntime().exec(args); // start the game
    }
}
