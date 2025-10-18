package wtf.kennn.gideonFly.Managers;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import wtf.kennn.gideonFly.GideonFly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

public class ConfigManager {

    private final GideonFly plugin;
    private File configFile, spawnFile, playerDataFile;
    private FileConfiguration config, spawn, playerData;

    public ConfigManager(GideonFly plugin) {
        this.plugin = plugin;
        createFiles();
    }

    private void createFiles() {
        createFile("config.yml");
        createFile("spawn.yml");
        createFile("playerdata.yml");

        configFile = new File(plugin.getDataFolder(), "config.yml");
        spawnFile = new File(plugin.getDataFolder(), "spawn.yml");
        playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");

        config = YamlConfiguration.loadConfiguration(configFile);
        spawn = YamlConfiguration.loadConfiguration(spawnFile);
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    private void createFile(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try (InputStream in = plugin.getResource(name)) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                    plugin.getLogger().info("✅ Created " + name);
                } else {
                    plugin.getLogger().warning("⚠ Resource " + name + " not found inside jar!");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("❌ Failed to create " + name + ": " + e.getMessage());
            }
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getSpawn() {
        return spawn;
    }

    public FileConfiguration getPlayerData() {
        return playerData;
    }

    public void savePlayerData() {
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Could not save playerdata.yml: " + e.getMessage());
        }
    }

    public void reloadAll() {
        config = YamlConfiguration.loadConfiguration(configFile);
        spawn = YamlConfiguration.loadConfiguration(spawnFile);
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);


        try (InputStream defConfigStream = plugin.getResource("config.yml")) {
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
                config.setDefaults(defConfig);
            }
        } catch (IOException ignored) {}
    }
}
