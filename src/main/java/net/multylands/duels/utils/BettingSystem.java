package net.multylands.duels.utils;

import net.milkbowl.vault.economy.Economy;
import net.multylands.duels.Duels;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

public class BettingSystem {
    public static Economy econ = null;

    public static void setupEconomy(Duels plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().log(Level.INFO, "You don't have vault plugin on the server. Betting system is disabled.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().log(Level.INFO, "Found no economy provider.");
            return;
        }
        econ = rsp.getProvider();
    }

    public static void execTakeMoneyCommands(Duels plugin, double amount, String playerName) {
        for (String command : plugin.getConfig().getStringList("game.betting.bet-take-commands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%player%", playerName));
        }
    }

    public static void execGiveMoneyCommands(Duels plugin, double amount, String playerName) {
        for (String command : plugin.getConfig().getStringList("game.betting.bet-give-commands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%player%", playerName));
        }
    }
}
