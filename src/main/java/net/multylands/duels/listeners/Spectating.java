package net.multylands.duels.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.GameUtils;
import net.multylands.duels.utils.RequestUtils;
import net.multylands.duels.utils.SpectatorUtils;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;

public class Spectating implements Listener {
    Duels plugin;

    public Spectating(Duels plugin) {
        this.plugin = plugin;
    }

    //prevents spectators from moving too far from the arena(actually from spectated players)
    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player playerWhoMoved = event.getPlayer();
        UUID uuid = playerWhoMoved.getUniqueId();
        if (!MemoryStorage.spectators.containsKey(uuid)) {
            return;
        }
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(MemoryStorage.spectators.get(uuid));
        if (request == null) {
            SpectatorUtils.endSpectating(playerWhoMoved, plugin);
            event.setCancelled(true);
            return;
        }
        Location loc1 = request.getGame().getArena().getFirstLocation(plugin);
        if (playerWhoMoved.getLocation().distance(loc1) < 50) {
            return;
        }
        //this is slightly more optimized than getting both loc at the same time
        Location loc2 = request.getGame().getArena().getSecondLocation(plugin);
        if (playerWhoMoved.getLocation().distance(loc2) < 50) {
            return;
        }
        GameUtils.clearPassengers(plugin, playerWhoMoved);
        playerWhoMoved.teleport(loc1);
        event.setCancelled(true);
    }

    //quit spectating & tp player to spawn after he quits
    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player playerWhoLeft = event.getPlayer();
        UUID playerWhoLeftUUID = playerWhoLeft.getUniqueId();
        if (!MemoryStorage.spectators.containsKey(playerWhoLeftUUID)) {
            return;
        }
        SpectatorUtils.endSpectating(playerWhoLeft, plugin);
    }

    //anti item pickup by spectator
    @EventHandler(ignoreCancelled = true)
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        Player spectator = event.getPlayer();
        UUID spectatorUUID = spectator.getUniqueId();
        if (!MemoryStorage.spectators.containsKey(spectatorUUID)) {
            return;
        }
        event.setCancelled(true);
        Chat.sendMessage(spectator, plugin.languageConfig.getString("duel.spectating.cannot-pickup-in-spectator"));
    }

    //anti item drop by spectator
    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player spectator = event.getPlayer();
        UUID spectatorUUID = spectator.getUniqueId();
        if (!MemoryStorage.spectators.containsKey(spectatorUUID)) {
            return;
        }
        event.setCancelled(true);
        Chat.sendMessage(spectator, plugin.languageConfig.getString("duel.spectating.cannot-drop-in-spectator"));
    }

    //anti command
    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player commandSender = event.getPlayer();
        UUID commandSenderUUID = commandSender.getUniqueId();
        if (!MemoryStorage.spectators.containsKey(commandSenderUUID)) {
            return;
        }
        String command = event.getMessage();
        boolean ifMatchesAny = false;
        for (String whitelisted_or_blacklisted_Command : plugin.getConfig().getStringList("spectator.commands.commands")) {
            if (command.equalsIgnoreCase(whitelisted_or_blacklisted_Command)) {
                ifMatchesAny = true;
                break;
            }
        }
        String blockMessage = plugin.languageConfig.getString("duel.spectating.no-commands");
        if (plugin.getConfig().getBoolean("spectator.commands.mode")) {
            //then this command is whitelisted
            if (ifMatchesAny) {
                return;
            }
            Chat.sendMessage(commandSender, blockMessage);
            event.setCancelled(true);
            return;
        }
        //then this command is not in the blacklist
        if (!ifMatchesAny) {
            return;
        }
        Chat.sendMessage(commandSender, blockMessage);
        event.setCancelled(true);
    }

    //preventing death of spectator
    @EventHandler(ignoreCancelled = true)
    public void onSpectatorsDamage(EntityDamageEvent event) {
        UUID damagedUUID = event.getEntity().getUniqueId();
        if (!MemoryStorage.spectators.containsKey(damagedUUID)) {
            return;
        }
        event.setCancelled(true);
    }

    //prevent spectator from hurting anyone
    @EventHandler(ignoreCancelled = true)
    public void onSpectatorDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) {
            return;
        }
        UUID damagerUUID = damager.getUniqueId();
        if (!MemoryStorage.spectators.containsKey(damagerUUID)) {
            return;
        }
        event.setCancelled(true);
        Chat.sendMessage(damager, plugin.languageConfig.getString("duel.spectating.cannot-damage-in-duel"));
    }

    //anti teleport
    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!MemoryStorage.spectators.containsKey(playerUUID)) {
            return;
        }
        SpectatorUtils.endSpectating(player, plugin);
    }

    //prevent projectile damage from spectator
    @EventHandler(ignoreCancelled = true)
    public void onProjectile(ProjectileLaunchEvent event) {
        ProjectileSource shooterEntity = event.getEntity().getShooter();
        if (!(shooterEntity instanceof Player player)) {
            return;
        }
        UUID playerUUID = player.getUniqueId();
        if (!MemoryStorage.spectators.containsKey(playerUUID)) {
            return;
        }
        event.setCancelled(true);
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.spectating.cannot-damage-in-duel"));
    }

}
