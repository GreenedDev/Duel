package net.multylands.duels.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.multylands.duels.Duels;
import net.multylands.duels.commands.DuelAdminCommand;
import net.multylands.duels.commands.DuelsCommand;
import net.multylands.duels.commands.player.request.SendCommand;
import net.multylands.duels.commands.admin.ReloadCommand;
import net.multylands.duels.commands.admin.SetSpawnCommand;
import net.multylands.duels.commands.admin.arena.ArenaListCommand;
import net.multylands.duels.commands.admin.arena.CreateArenaCommand;
import net.multylands.duels.commands.admin.arena.DeleteArenaCommand;
import net.multylands.duels.commands.admin.arena.SetPosCommand;
import net.multylands.duels.commands.player.ignore.IgnoreCommand;
import net.multylands.duels.commands.player.queue.QueueCommand;
import net.multylands.duels.commands.player.request.AcceptCommand;
import net.multylands.duels.commands.player.request.CancelCommand;
import net.multylands.duels.commands.player.request.DenyCommand;
import net.multylands.duels.commands.player.spectate.SpectateCommand;
import net.multylands.duels.commands.player.spectate.StopSpectateCommand;
import net.multylands.duels.gui.listeners.ArenaGUIListener;
import net.multylands.duels.gui.listeners.DuelGUIListener;
import net.multylands.duels.listeners.*;
import net.multylands.duels.placeholders.PlaceholderAPI;
import net.multylands.duels.queue.QueueListener;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class ServerUtils {
    public static void registerListeners(Duels plugin) {
        plugin.getServer().getPluginManager().registerEvents(new DuelGUIListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new Game(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new Spectating(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new UpdateListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new Restrictions(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ArenaGUIListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new QueueListener(), plugin);
    }

    public static void registerCommands(Duels plugin) {
        plugin.getCommand("duel").setExecutor(new SendCommand(plugin.guiManager, plugin));
        plugin.getCommand("duels").setExecutor(new DuelsCommand(plugin));
        //player commands
        MemoryStorage.playerCommandExecutors.put("accept", new AcceptCommand(plugin));
        MemoryStorage.playerCommandExecutors.put("cancel", new CancelCommand(plugin));
        MemoryStorage.playerCommandExecutors.put("deny", new DenyCommand(plugin));
        MemoryStorage.playerCommandExecutors.put("ignore", new IgnoreCommand(plugin));
        MemoryStorage.playerCommandExecutors.put("spectate", new SpectateCommand(plugin));
        MemoryStorage.playerCommandExecutors.put("stopspectate", new StopSpectateCommand(plugin));
        MemoryStorage.playerCommandExecutors.put("queue", new QueueCommand(plugin));
        //admin commands
        plugin.getCommand("dueladmin").setExecutor(new DuelAdminCommand(plugin));
        MemoryStorage.adminCommandExecutors.put("reload", new ReloadCommand(plugin));
        MemoryStorage.adminCommandExecutors.put("setarenapos", new SetPosCommand(plugin));
        MemoryStorage.adminCommandExecutors.put("setspawn", new SetSpawnCommand(plugin));
        MemoryStorage.adminCommandExecutors.put("createarena", new CreateArenaCommand(plugin));
        MemoryStorage.adminCommandExecutors.put("deletearena", new DeleteArenaCommand(plugin));
        MemoryStorage.adminCommandExecutors.put("arenalist", new ArenaListCommand(plugin));
        MemoryStorage.adminCommandExecutors.put("help", new DuelAdminCommand(plugin));
    }

    public static boolean isPaper() {
        boolean isPaper = false;
        try {
            // Any other works, just the shortest I could find.
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        } catch (ClassNotFoundException ignored) {

        }
        return isPaper;
    }

    public static void implementBStats(Duels plugin) {
        Metrics metrics = new Metrics(plugin, 21176);
        metrics.addCustomChart(new SingleLineChart("servers", () -> {
            return 1;
        }));
    }

    public static void implementPlaceholderAPI(Duels plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new PlaceholderAPI(plugin).register(); //
        } else {
            plugin.getLogger().log(Level.WARNING, "Could not find PlaceholderAPI! You wouldn't be able to use plugin's placeholders.");
        }
    }

    public static MiniMessage miniMessage() {
        if (Duels.miniMessage == null) {
            throw new IllegalStateException("miniMessage is null when getting it from the main class");
        }
        return Duels.miniMessage;
    }
}
