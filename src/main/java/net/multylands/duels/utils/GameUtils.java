package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.object.Game;
import net.multylands.duels.utils.storage.SavingItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GameUtils {
    public static void disableShieldsIfDisabled(Duels plugin, DuelRestrictions restrictions, Player sender, Player target) {
        if (restrictions.isShieldAllowed()) {
            return;
        }
        int maxDuelTimeInTicks = plugin.getConfig().getInt("game.max_duel_time") * 60 * 20;
        sender.setShieldBlockingDelay(maxDuelTimeInTicks);
        target.setShieldBlockingDelay(maxDuelTimeInTicks);
    }

    public static void resetShieldsDelay(Duels plugin, DuelRestrictions restrictions, Player player) {
        if (player == null) {
            return;
        }
        if (restrictions.isShieldAllowed()) {
            return;
        }
        player.setShieldBlockingDelay(plugin.getConfig().getInt("default-shield-blocking-delay"));
    }

    public static void removeEffectsIfDisabled(DuelRestrictions restrictions, Player sender, Player target) {
        if (restrictions.isPotionAllowed()) {
            return;
        }
        for (PotionEffect effect : sender.getActivePotionEffects()) {
            sender.removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : target.getActivePotionEffects()) {
            target.removePotionEffect(effect.getType());
        }
    }

    public static void disableFlying(Player sender, Player target) {
        sender.setFlying(false);
        sender.setAllowFlight(false);
        target.setFlying(false);
        target.setAllowFlight(false);
    }

    public static void executeEndCommands(Duels plugin, DuelRestrictions restrictions, Player winner, Player loser, String arenaName) {
        if (restrictions.isInventorySavingEnabled()) {
            for (String commandFromTheLoop : plugin.getConfig().getStringList("game.commands.end.inventory-saving-enabled")) {
                String command = commandFromTheLoop
                        .replace("%winner%", winner.getName())
                        .replace("%loser%", loser.getName())
                        .replace("%arena_name%", arenaName);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
            return;
        }
        for (String commandFromTheLoop : plugin.getConfig().getStringList("game.commands.end.normal")) {
            String command = commandFromTheLoop
                    .replace("%winner%", winner.getName())
                    .replace("%loser%", loser.getName())
                    .replace("%arena_name%", arenaName);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public static void executeStartCommands(Duels plugin, DuelRestrictions restrictions, Player senderPlayer, Player targetPlayer, String arenaName) {
        if (restrictions.isInventorySavingEnabled()) {
            for (String commandFromTheLoop : plugin.getConfig().getStringList("game.commands.start.inventory-saving-enabled")) {
                String command = commandFromTheLoop
                        .replace("%player1%", senderPlayer.getName())
                        .replace("%player2%", targetPlayer.getName())
                        .replace("%arena_name%", arenaName);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
            return;
        }
        for (String commandFromTheLoop : plugin.getConfig().getStringList("game.commands.start.normal")) {
            String command = commandFromTheLoop
                    .replace("%player1%", senderPlayer.getName())
                    .replace("%player2%", targetPlayer.getName())
                    .replace("%arena_name%", arenaName);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public static void startCounting(Duels plugin, Game game, Player senderPlayer, Player targetPlayer) {
        AtomicInteger countdown = new AtomicInteger(6);
        Duels.scheduler.runTaskTimer(plugin, task -> {
            String color = Chat.getColorForNumber(countdown);
            countdown.getAndDecrement();
            if (countdown.get() == 0) {
                game.setIsStartingIn5Seconds(false);
                task.cancel();
                Chat.messagePlayers(senderPlayer, targetPlayer, plugin.languageConfig.getString("duel.game.duel-started"));
            } else {
                Chat.messagePlayers(senderPlayer, targetPlayer, plugin.languageConfig.getString("duel.game.duel-countdown").replace("%color+countdown%", color + countdown));
            }
        }, 0, 20);
    }

    public static void removeSpectatorsFromGame(Duels plugin, List<UUID> spectators) {
        for (UUID spectatorUUID : spectators) {
            SpectatorUtils.endSpectatingForEndGame(Bukkit.getPlayer(spectatorUUID), plugin);
        }
        spectators.clear();
    }

    public static void teleportToSpawn(Duels plugin, Player player) {
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        player.teleport(spawnLoc);
    }

    public static void applyInventorySavingIfEnabled(DuelRestrictions restrictions, Player player) {
        if (!restrictions.isInventorySavingEnabled()) {
            return;
        }
        SavingItems.saveAndClearInventoryIfEnabled(player);
    }

    public static void reverseInventorySavingIfEnabled(DuelRestrictions restrictions, Player player) {
        if (!restrictions.isInventorySavingEnabled()) {
            return;
        }
        SavingItems.clearInvAndGiveItemsBack(player);
    }
}
