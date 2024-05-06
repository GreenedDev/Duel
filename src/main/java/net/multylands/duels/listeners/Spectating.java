package net.multylands.duels.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import net.multylands.duels.utils.SpectatorUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
        if (!Duels.spectators.containsKey(uuid)) {
            return;
        }
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(Duels.spectators.get(uuid));
        Location loc1 = request.getGame().getArena().getFirstLocation(plugin);
        if (playerWhoMoved.getLocation().distance(loc1) < 50) {
            return;
        }
        //this is slightly more optimized than getting both loc at the same time
        Location loc2 = request.getGame().getArena().getSecondLocation(plugin);
        if (playerWhoMoved.getLocation().distance(loc2) < 50) {
            return;
        }
        playerWhoMoved.teleport(loc1);
        event.setCancelled(true);
    }

    //blocks damage from spectator to spectated player
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity attackerEntity = event.getDamager();
        if (!(entity instanceof Player)) {
            return;
        }
        if (!(attackerEntity instanceof Player)) {
            return;
        }
        Player victim = ((Player) entity).getPlayer();
        Player attacker = ((Player) attackerEntity).getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(victim.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getOpponent(victim.getUniqueId()) == attacker.getUniqueId()) {
            return;
        }
        Chat.sendMessage(attacker, plugin.languageConfig.getString("duel.spectating.cannot-damage-in-duel"));
        event.setCancelled(true);
    }

    //quit spectating & tp player to spawn after he quits
    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player playerWhoLeft = event.getPlayer();
        UUID playerWhoLeftUUID = playerWhoLeft.getUniqueId();
        if (!Duels.spectators.containsKey(playerWhoLeftUUID)) {
            return;
        }
        SpectatorUtils.endSpectating(playerWhoLeft, plugin);
    }

    //anti command
    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player commandSender = event.getPlayer();
        UUID commandSenderUUID = commandSender.getUniqueId();
        if (!Duels.spectators.containsKey(commandSenderUUID)) {
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
    public void onDamage(EntityDamageEvent event) {
        UUID damagedUUID = event.getEntity().getUniqueId();
        if (!Duels.spectators.containsKey(damagedUUID)) {
            return;
        }
        event.setCancelled(true);
    }

    //anti teleport
    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!Duels.spectators.containsKey(playerUUID)) {
            return;
        }
        SpectatorUtils.endSpectating(player, plugin);
    }

    //prevent projectile damage from spectator
    @EventHandler(ignoreCancelled = true)
    public void onTeleport(ProjectileLaunchEvent event) {
        ProjectileSource shooterEntity = event.getEntity().getShooter();
        if (!(shooterEntity instanceof Player)) {
            return;
        }
        Player player = ((Player) shooterEntity).getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!Duels.spectators.containsKey(playerUUID)) {
            return;
        }
        event.setCancelled(true);
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.spectating.cannot-damage-in-duel"));
    }
}
