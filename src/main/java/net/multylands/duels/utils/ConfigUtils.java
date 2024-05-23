package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {
    Duels plugin;

    public static String ignoresFileName = "ignores.yml";
    public static String arenasFileName = "arenas.yml";
    public static String configFileName = "config.yml";
    public static String languageFileName = "language.yml";

    public ConfigUtils(Duels plugin) {
        this.plugin = plugin;
    }

    public void addMissingKeysAndValues(FileConfiguration config, String fileName) {
        List<String> KeysAndValues = getKeysAndValues(fileName);
        for (String key : config.getKeys(true)) {
            KeysAndValues.remove(key);
        }
        if (KeysAndValues.isEmpty()) {
            return;
        }
        FileConfiguration defaultConfig;
        try {
            defaultConfig = getConfigFromResource(fileName);
            for (String actuallyMissingKey : KeysAndValues) {
                config.set(actuallyMissingKey, defaultConfig.get(actuallyMissingKey));
                File configFile = new File(plugin.getDataFolder(), fileName);
                config.save(configFile);
            }
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getKeysAndValues(String resourceName) {
        FileConfiguration config = new YamlConfiguration();
        try {
            config = getConfigFromResource(resourceName);
        } catch (InvalidConfigurationException | IOException e) {
            System.out.println(e);
        }
        return new ArrayList<>(config.getKeys(true));
    }

    public FileConfiguration getConfigFromResource(String resourceName) throws IOException, InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        InputStream stream = plugin.getResource(resourceName);
        Reader reader = new InputStreamReader(stream);
        config.load(reader);
        reader.close();
        stream.close();
        return config;
    }
}
