package net.multylands.duels;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.multylands.duels.gui.GUIManager;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.placeholders.MiniPlaceholders;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.ConfigUtils;
import net.multylands.duels.utils.ServerUtils;
import net.multylands.duels.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Duels extends JavaPlugin {
    public static HashMap<String, Arena> Arenas = new HashMap<>();
    //storing only sender: request(withTargetName)
    public static HashMap<UUID, Set<DuelRequest>> requestsReceiverToSenders = new HashMap<>();
    public static HashMap<UUID, Set<DuelRequest>> requestsSenderToReceivers = new HashMap<>();
    //storing sender: player
    //and player: sender
    public static HashMap<UUID, UUID> playerToOpponentInGame = new HashMap<>();
    //storing uuid: taskID
    public static HashMap<UUID, Integer> tasksToCancel = new HashMap<>();
    //storing spectator: toSpectate
    public static HashMap<UUID, UUID> spectators = new HashMap<>();
    public String newVersion = null;
    public int duelInventorySize;
    public File ignoresFile;
    public String ignoresFileName = "ignores.yml";
    public File arenasFile;
    public String arenasFileName = "arenas.yml";
    public File configFile;
    public String configFileName = "config.yml";
    public File languageFile;
    public String languageFileName = "language.yml";
    public static MiniMessage miniMessage;
    public FileConfiguration ignoresConfig;
    public FileConfiguration arenasConfig;
    public FileConfiguration languageConfig;
    public static BukkitScheduler scheduler = Bukkit.getScheduler();
    public GUIManager manager;
    public static BukkitAudiences adventure;
    public static HashMap<String, CommandExecutor> commandExecutors = new HashMap<>();

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);
        miniMessage = MiniMessage.miniMessage();
        if (!ServerUtils.isPaper(this)) {
            getLogger().info("Server isn't running the PAPER software which means " +
                    "i can't use it's API to deal with shield restrictions. Please switch to " +
                    "paper otherwise Shield restriction will be disabled.");
        }
        checkForUpdates();
        createConfigs();
        MiniPlaceholders.implementMiniPlaceholders(this);
        ServerUtils.implementBStats(this);
        ServerUtils.implementPlaceholderAPI(this);
        manager = new GUIManager(this);
        ServerUtils.registerListeners(this);
        ServerUtils.registerCommands(this);
    }

    @Override
    public void onDisable() {
        for (Set<DuelRequest> requestsSet : requestsReceiverToSenders.values()) {
            for (DuelRequest request : requestsSet) {
                if (!request.getGame().getIsInGame()) {
                    continue;
                }
                request.getGame().endGameRestart();
            }
        }
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    private void createConfigs() {
        try {
            ConfigUtils configUtils = new ConfigUtils(this);
            ignoresFile = new File(getDataFolder(), ignoresFileName);
            arenasFile = new File(getDataFolder(), arenasFileName);
            configFile = new File(getDataFolder(), configFileName);
            languageFile = new File(getDataFolder(), languageFileName);
            //we are checking if files exist to avoid console spamming. try it and see :)
            if (!ignoresFile.exists()) {
                saveResource(ignoresFileName, false);
            }
            if (!languageFile.exists()) {
                saveResource(languageFileName, false);
            }
            if (!configFile.exists()) {
                saveDefaultConfig();
            }
            if (!arenasFile.exists()) {
                saveResource(arenasFileName, false);
            }
            arenasConfig = new YamlConfiguration();
            ignoresConfig = new YamlConfiguration();
            languageConfig = new YamlConfiguration();

            ignoresConfig.load(ignoresFile);
            arenasConfig.load(arenasFile);
            languageConfig.load(languageFile);
            getConfig().load(configFile);
            configUtils.addMissingKeysAndValues(getConfig(), configFileName);
            configUtils.addMissingKeysAndValues(ignoresConfig, ignoresFileName);
            configUtils.addMissingKeysAndValues(arenasConfig, arenasFileName);
            configUtils.addMissingKeysAndValues(languageConfig, languageFileName);
            loadArenas();
            duelInventorySize = languageConfig.getInt("duel-GUI.size");
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
        arenasFile = new File(getDataFolder(), arenasFileName);
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        Arenas.clear();
        loadArenas();
    }

    public void loadArenas() {
        for (Set<DuelRequest> requestsSet : requestsReceiverToSenders.values()) {
            for (DuelRequest request : requestsSet) {
                if (!request.getGame().getIsInGame()) {
                    continue;
                }
                request.getGame().endGameRestart();
            }
        }
        for (String arenaID : arenasConfig.getKeys(false)) {
            if (arenasConfig.getLocation(arenaID + ".pos1") == null
                    || arenasConfig.getLocation(arenaID + ".pos2") == null) {
                continue;
            }
            Location loc1 = arenasConfig.getLocation(arenaID + ".pos1");
            Location loc2 = arenasConfig.getLocation(arenaID + ".pos2");
            Arena arena = new Arena(loc1, loc2, null, null, arenaID);
            Arenas.put(arenaID, arena);
        }
    }

    public void reloadLanguageConfig() {
        languageFile = new File(getDataFolder(), languageFileName);
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    }
}