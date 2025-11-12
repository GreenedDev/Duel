package net.multylands.duels.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.GameUtils;
import net.multylands.duels.utils.RequestUtils;
import net.multylands.duels.utils.storage.MemoryStorage;
import net.multylands.duels.utils.storage.PersistentDataUtils;
import net.multylands.duels.utils.storage.SavingItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;
import java.util.logging.Level;

public class Game implements Listener {
    Duels plugin;

    public Game(Duels plugin) {
        this.plugin = plugin;
    }

    public Player getActualDamager(Entity attackerEntity) {
        if (attackerEntity instanceof Player attackerPlayer) {
            return attackerPlayer;
        } else if (attackerEntity instanceof Projectile projectile) {
            ProjectileSource shooterEntity = projectile.getShooter();
            if (shooterEntity instanceof Player shooter) {
                return shooter;
            } else {
                return null;
            }
        }
    }

    //blocks damage to duel-player from non duel-players
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity attackerEntity = event.getDamager();
        if (!(entity instanceof Player victim)) {
            return;
        }
        UUID victimUUID = victim.getUniqueId();

        //allow damage if player isn't in duel
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(victimUUID);
        if (!RequestUtils.isInGame(request)) {
            if (request == null) {
                return;
            }
            if (!request.getGame().getIsAboutToTeleportedToSpawn()) {
                return;
            }
        }
        //at this point we are sure that damaged entity is a player and is in a duel or at least in the duel arena
        Player attacker = getActualDamager(attackerEntity);
        if (attacker == null) {
            plugin.getLogger().log(Level.SEVERE, "unimplemented damage type: " + event.getDamageSource().getDamageType());
            event.setCancelled(true);
            return;
        }
        UUID attackerUUID = attacker.getUniqueId();
        //check if victim is getting damaged in the 10 seconds item pickup period
        if (request.getGame().getIsAboutToTeleportedToSpawn() && victimUUID == request.getGame().getWinnerUUID()) {
            Chat.sendMessage(attacker, plugin.languageConfig.getString("duel.spectating.cannot-damage-in-duel"));
            event.setCancelled(true);
            return;
        }
        if (request.getOpponent(attackerUUID) == victimUUID) { //pass the event if duel opponents are fighting each other
            return;
        }
        Chat.sendMessage(attacker, plugin.languageConfig.getString("duel.spectating.cannot-damage-in-duel"));
        event.setCancelled(true);
    }

    //prevent moving for during countdown
    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (request == null) {
            return;
        }
        if (!request.getGame().getIsStartingIn5Seconds()) {
            return;
        }
        event.setCancelled(true);
    }

    //prevent items getting damaged in duel if keepinv is on
    @EventHandler(ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (!request.getGame().getRestrictions().isKeepInventoryEnabled()) {
            return;
        }
        if (!plugin.getConfig().getBoolean("modules.prevent-item-damage-during-duel")) {
            return;
        }
        event.setCancelled(true);
    }

    //handle duel player quitting the game
    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player playerWhoLeft = event.getPlayer();
        UUID playerWhoLeftUUID = playerWhoLeft.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerWhoLeftUUID);
        if (request == null) {
            return;
        }
        PersistentDataUtils.markPlayerAsQuitter(plugin, playerWhoLeft);
        UUID winner = request.getOpponent(playerWhoLeftUUID);
        if (!request.getGame().getRestrictions().isKeepInventoryEnabled()) {
            playerWhoLeft.setHealth(0);
        }
        request.getGame().endGame(winner, true);
    }

    //anti arena stayer
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player playerWhoLeft = event.getPlayer();
        if (!PersistentDataUtils.isPlayerQuitter(plugin, playerWhoLeft)) {
            return;
        }
        SavingItems.giveItemsBackIfAvailable(playerWhoLeft);
        GameUtils.teleportToSpawn(plugin, playerWhoLeft);
    }

    //anti command
    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (request == null) {
            return;
        }
        if (request.getGame().getIsAboutToTeleportedToSpawn()) {
            if (playerUUID != request.getGame().getWinnerUUID()) {
                return;
            }
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

    //handling death
    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity().getPlayer();
        UUID deadUUID = dead.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(deadUUID);

        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isInventorySavingEnabled() || request.getGame().getRestrictions().isKeepInventoryEnabled()) {
            event.getDrops().clear();
        }
        if (request.getGame().getRestrictions().isKeepInventoryEnabled()) {
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
        UUID winnerUUID = request.getOpponent(deadUUID);
        request.getGame().endGame(winnerUUID, false);
    }

    //anti item pickup of items thrown by non-duel players. this is just in case someone gains access to the arena.
    @EventHandler(ignoreCancelled = true)
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        Player pickupPlayer = event.getPlayer();
        UUID pickupPlayerUUID = pickupPlayer.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(pickupPlayerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        UUID throwerUUID = event.getItem().getThrower();

        //if item isn't thrown by players involved in this duel, duel players cant pick up this item
        if (pickupPlayer.getUniqueId() != throwerUUID && request.getOpponent(pickupPlayerUUID) != throwerUUID) {
            Chat.sendMessage(pickupPlayer, plugin.languageConfig.getString("duel.game.deny-pickup-of-other-items"));
            event.setCancelled(true);
            return;
        }
    }

    //anti teleport for in duel players
    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        event.setCancelled(true);
    }

    //give items back on respawn if its enabled
    @EventHandler(ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        SavingItems.giveItemsBackIfAvailable(player);
        if (MemoryStorage.playersWhoShouldBeTeleportedToSpawnAfterRespawn.contains(playerUUID)) {
            MemoryStorage.playersWhoShouldBeTeleportedToSpawnAfterRespawn.remove(playerUUID);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                GameUtils.teleportToSpawn(plugin, player);
            }, 2L);
        }
    }
}
