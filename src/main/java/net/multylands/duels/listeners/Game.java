package net.multylands.duels.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

public class Game implements Listener {
    Duels plugin;

    public Game(Duels plugin) {
        this.plugin = plugin;
    }

    //prevent moving for during countdown
    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (!request.getGame().getIsStartingIn5Seconds()) {
            return;
        }
        event.setCancelled(true);
    }

    //anti leave
    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player playerWhoLeft = event.getPlayer();
        UUID playerWhoLeftUUID = playerWhoLeft.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerWhoLeft.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        UUID winner = request.getOpponent(playerWhoLeftUUID);
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        playerWhoLeft.teleport(spawnLoc);
        request.getGame().endGame(winner);
        playerWhoLeft.setHealth(0);
    }

    //anti command
    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        String command = event.getMessage();
        boolean ifMatchesAny = false;
        for (String whitelisted_or_blacklisted_Command : plugin.getConfig().getStringList("game.commands.blocked-or-allowed-commands.commands")) {
            if (command.equalsIgnoreCase(whitelisted_or_blacklisted_Command)) {
                ifMatchesAny = true;
                break;
            }
        }
        String blockMessage = plugin.languageConfig.getString("duel.this-command-blocked");
        if (plugin.getConfig().getBoolean("game.commands.blocked-or-allowed-commands.mode")) {
            //then this command is whitelisted
            if (ifMatchesAny) {
                return;
            }
            Chat.sendMessage(player, blockMessage);
            event.setCancelled(true);
            return;
        }
        //then this command is not in the blacklist
        if (!ifMatchesAny) {
            return;
        }
        Chat.sendMessage(player, blockMessage);
        event.setCancelled(true);
    }

    //anti command
    @EventHandler(ignoreCancelled = true)
    public void onCommandForWinner(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player.getUniqueId());
        //do not check this with requestutils.isingame because when we run endGame method we set game as ended.
        if (request == null) {
            return;
        }
        if (!request.getGame().getIsAboutToTeleportedToSpawn()) {
            return;
        }
        if (player.getUniqueId() != request.getGame().getWinnerUUID()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.this-command-blocked"));
        event.setCancelled(true);
    }

    //handling death
    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity().getPlayer();
        UUID deadUUID = dead.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(dead.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isKeepInventoryEnabled()) {
            event.setKeepInventory(true);
            event.getDrops().clear();
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
        UUID winnerUUID = request.getOpponent(deadUUID);
        request.getGame().endGame(winnerUUID);
    }

    //anti teleport
    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        //ender pearl restriction moved to Restrictions.java
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        event.setCancelled(true);
    }

    //give items back on respawn if its enabled
    @EventHandler(ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        SavingItems.giveItemsBackIfAvailable(player);
        if (MemoryStorage.listOfPlayersWhoShouldBeTeleportedToSpawnAfterRespawn.contains(player.getUniqueId())) {
            MemoryStorage.listOfPlayersWhoShouldBeTeleportedToSpawnAfterRespawn.remove(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                GameUtils.teleportToSpawn(plugin, player);
            }, 2L);
        }
    }
}
