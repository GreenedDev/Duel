package net.multylands.duels;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.multylands.duels.gui.GUIManager;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.placeholders.MiniPlaceholders;
import net.multylands.duels.utils.BettingSystem;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.ServerUtils;
import net.multylands.duels.utils.UpdateChecker;
import net.multylands.duels.utils.storage.ConfigUtils;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;

public class Duels extends JavaPlugin {
    public String newVersion = null;
    public int duelInventorySize;
    public int arenaInventorySize;
    public File ignoresFile;
    public File arenasFile;
    public File configFile;
    public File languageFile;
    public static MiniMessage miniMessage;
    public FileConfiguration ignoresConfig;
    public FileConfiguration arenasConfig;
    public FileConfiguration languageConfig;
    public static BukkitScheduler scheduler = Bukkit.getScheduler();
    public static GUIManager guiManager;

    @Override
    public void onEnable() {
        BettingSystem.setupEconomy(this);
        miniMessage = MiniMessage.miniMessage();
        if (!ServerUtils.isPaper()) {
            getLogger().info("Server isn't running the PAPER software which means " +
                    "i can't use it's API to deal with shield restrictions. Please switch to " +
                    "paper otherwise Shield restriction will be disabled.");
        }
        checkForUpdates();
        createConfigs();
        MiniPlaceholders.implementMiniPlaceholders(this);
        ServerUtils.implementBStats(this);
        ServerUtils.implementPlaceholderAPI(this);
        guiManager = new GUIManager(this);
        ServerUtils.registerListeners(this);
        ServerUtils.registerCommands(this);
    }

    @Override
    public void onDisable() {
        for (DuelRequest request : MemoryStorage.inGameDuels) {
            request.getGame().endGameRestart();
        }
    }

    private void createConfigs() {
        try {
            ConfigUtils configUtils = new ConfigUtils(this);
            ignoresFile = new File(getDataFolder(), ConfigUtils.ignoresFileName);
            arenasFile = new File(getDataFolder(), ConfigUtils.arenasFileName);
            configFile = new File(getDataFolder(), ConfigUtils.configFileName);
            languageFile = new File(getDataFolder(), ConfigUtils.languageFileName);
            //we are checking if files exist to avoid console spamming. try it and see :)
            if (!ignoresFile.exists()) {
                saveResource(ConfigUtils.ignoresFileName, false);
            }
            if (!languageFile.exists()) {
                saveResource(ConfigUtils.languageFileName, false);
            }
            if (!configFile.exists()) {
                saveDefaultConfig();
            }
            if (!arenasFile.exists()) {
                saveResource(ConfigUtils.arenasFileName, false);
            }
            arenasConfig = new YamlConfiguration();
            ignoresConfig = new YamlConfiguration();
            languageConfig = new YamlConfiguration();

            ignoresConfig.load(ignoresFile);
            arenasConfig.load(arenasFile);
            languageConfig.load(languageFile);

            getConfig().load(configFile);
            if (getConfig().getLocation("spawn_location") == null) {
                if (Bukkit.getWorld("world") == null) {
                    Location spawnLoc = new Location(Bukkit.getWorlds().get(0), 0, 90, 0);
                    getConfig().set("spawn_location", spawnLoc);
                    saveConfig();
                } else {
                    Location spawnLoc = new Location(Bukkit.getWorld("world"), 0, 90, 0);
                    getConfig().set("spawn_location", spawnLoc);
                    saveConfig();
                }
            }
            configUtils.addMissingKeysAndValues(getConfig(), ConfigUtils.configFileName);
            configUtils.addMissingKeysAndValues(ignoresConfig, ConfigUtils.ignoresFileName);
            configUtils.addMissingKeysAndValues(arenasConfig, ConfigUtils.arenasFileName);
            configUtils.addMissingKeysAndValues(languageConfig, ConfigUtils.languageFileName);
            loadArenas();
            duelInventorySize = languageConfig.getInt("duel-GUI.size");
            arenaInventorySize = languageConfig.getInt("arena-GUI.size");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveIgnoresConfig() {
        try {
            ignoresConfig.save(ignoresFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveArenasConfig() {
        try {
            arenasConfig.save(arenasFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkForUpdates() {
        new UpdateChecker(this, 114685).getVersion(version -> {
            if (!getDescription().getVersion().equals(version)) {
                newVersion = version;
                Chat.sendMessageSender(Bukkit.getConsoleSender(), languageConfig.getString("update-available").replace("%newversion%", version));
            }
        });
    }

    public void reloadArenaConfig() {
        arenasFile = new File(getDataFolder(), ConfigUtils.arenasFileName);
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        MemoryStorage.Arenas.clear();
        loadArenas();
    }

    public void loadArenas() {
        for (DuelRequest request : MemoryStorage.inGameDuels) {
            request.getGame().endGameRestart();
        }

        for (String arenaID : arenasConfig.getKeys(false)) {
            if (arenasConfig.getLocation(arenaID + ".pos1") == null
                    || arenasConfig.getLocation(arenaID + ".pos2") == null) {
                continue;
            }
            Location loc1 = arenasConfig.getLocation(arenaID + ".pos1");
            Location loc2 = arenasConfig.getLocation(arenaID + ".pos2");
            Arena arena = new Arena(loc1, loc2, null, null, arenaID);
            MemoryStorage.Arenas.put(arenaID, arena);
        }
    }

    public void reloadLanguageConfig() {
        languageFile = new File(getDataFolder(), ConfigUtils.languageFileName);
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    }
}