package net.multylands.duels.object;

import net.multylands.duels.Duels;
import net.multylands.duels.events.GameEndEvent;
import net.multylands.duels.events.GameStartEvent;
import net.multylands.duels.queue.QueueSystem;
import net.multylands.duels.utils.BettingSystem;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.GameUtils;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Game {
    UUID senderUUID;
    UUID targetUUID;

    UUID winnerUUID;
    DuelRestrictions restrictions;
    boolean isInGame;
    boolean isAboutToBeTeleportedToSpawn = false;
    double bet = 0;
    DuelRequest request;
    Duels plugin;
    List<UUID> spectators = new ArrayList<>();
    boolean isStartingIn5Seconds;
    Arena arena;
    int taskId = 0;
    Instant runOutOfTime;

    public Game(UUID sender, UUID target, DuelRequest request, DuelRestrictions duelRestrictions, boolean isInGame, boolean isStartingIn5Seconds, double bet, Duels plugin) {
        this.senderUUID = sender;
        this.targetUUID = target;
        this.request = request;
        this.bet = bet;
        this.isStartingIn5Seconds = isStartingIn5Seconds;
        this.restrictions = duelRestrictions;
        this.isInGame = isInGame;
        this.plugin = plugin;
    }

    public boolean getIsInGame() {
        return isInGame;
    }

    public boolean getIsStartingIn5Seconds() {
        return isStartingIn5Seconds;
    }

    public void setIsAboutToTeleportedToSpawn(boolean value) {
        isAboutToBeTeleportedToSpawn = value;
    }

    public boolean getIsAboutToTeleportedToSpawn() {
        return isAboutToBeTeleportedToSpawn;
    }

    public UUID getWinnerUUID() {
        return winnerUUID;
    }

    public void setWinnerUUID(UUID winner) {
        winnerUUID = winner;
    }

    public DuelRestrictions getRestrictions() {
        return restrictions;
    }

    public void setIsInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }

    public void setIsStartingIn5Seconds(boolean YesOrNot) {
        this.isStartingIn5Seconds = YesOrNot;
    }

    public void setRestrictions(DuelRestrictions restrictions) {
        this.restrictions = restrictions;
    }

    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
    }

    public void removeSpectator(UUID uuid) {
        spectators.remove(uuid);
    }

    public void start(Arena arena) {
        this.arena = arena;

        arena.setAvailable(false);
        arena.setSenderUUID(senderUUID);
        arena.setTargetUUID(targetUUID);
        setIsInGame(true);
        setIsStartingIn5Seconds(true);

        Player senderPlayer = Bukkit.getPlayer(senderUUID);
        Player targetPlayer = Bukkit.getPlayer(targetUUID);

        GameUtils.applyInventorySavingIfEnabled(restrictions, senderPlayer);
        GameUtils.applyInventorySavingIfEnabled(restrictions, targetPlayer);

        targetPlayer.teleport(arena.getFirstLocation(plugin));
        senderPlayer.teleport(arena.getSecondLocation(plugin));

        GameUtils.disableFlying(senderPlayer, targetPlayer);
        GameUtils.removeEffects(senderPlayer, targetPlayer);
        GameUtils.disableShieldsIfDisabled(plugin, restrictions, senderPlayer, targetPlayer);
        saveAndRunRanOutOfTimeTask();
        GameUtils.executeStartCommands(plugin, restrictions, senderPlayer, targetPlayer, arena.getID());
        GameUtils.startCounting(plugin, this, senderPlayer, targetPlayer);

        request.storeRequest(true);
        GameStartEvent event = new GameStartEvent(this, senderPlayer, targetPlayer);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void endGameRanOutOfTime() {
        GameUtils.removeSpectatorsFromGame(plugin, spectators);
        Player sender = Bukkit.getPlayer(senderUUID);
        Player target = Bukkit.getPlayer(targetUUID);
        GameUtils.resetShieldsDelay(plugin, restrictions, sender);
        GameUtils.resetShieldsDelay(plugin, restrictions, target);

        Chat.messagePlayers(sender, target, plugin.languageConfig.getString("duel.game.ran-out-of-time"));
        setIsInGame(false);
        GameUtils.teleportToSpawn(plugin, sender);
        GameUtils.teleportToSpawn(plugin, target);
        request.removeStoreRequest(true);
        GameUtils.reverseInventorySavingIfEnabled(restrictions, sender);
        GameUtils.reverseInventorySavingIfEnabled(restrictions, target);
        if (bet != 0) {
            BettingSystem.execGiveMoneyCommands(plugin, bet, sender.getName());
            BettingSystem.execGiveMoneyCommands(plugin, bet, target.getName());
            Chat.sendMessage(sender, plugin.languageConfig.getString("duel.betting.bet-added-back"));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.betting.bet-added-back"));
        }
        QueueSystem.checkQueue(plugin);
    }

    public void endGameRestart() {
        GameUtils.removeSpectatorsFromGame(plugin, spectators);
        Player sender = Bukkit.getPlayer(senderUUID);
        Player target = Bukkit.getPlayer(targetUUID);
        GameUtils.resetShieldsDelay(plugin, restrictions, sender);
        GameUtils.resetShieldsDelay(plugin, restrictions, target);


        Bukkit.getScheduler().cancelTask(MemoryStorage.tasksToCancel.get(senderUUID));
        MemoryStorage.tasksToCancel.remove(senderUUID);

        GameUtils.teleportToSpawn(plugin, sender);
        GameUtils.teleportToSpawn(plugin, target);
        request.removeStoreRequest(true);
        GameUtils.reverseInventorySavingIfEnabled(restrictions, sender);
        GameUtils.reverseInventorySavingIfEnabled(restrictions, target);

        if (bet != 0) {
            BettingSystem.execGiveMoneyCommands(plugin, bet, sender.getName());
            BettingSystem.execGiveMoneyCommands(plugin, bet, target.getName());
            Chat.sendMessage(sender, plugin.languageConfig.getString("duel.betting.bet-added-back"));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.betting.bet-added-back"));
        }
    }

    public void endGame(UUID winnerUUIDFromMethod) {
        if (!isInGame) { //check if game is already ended. maybe some of death-related events can happen at the same time.
            return;
        }
        GameUtils.removeSpectatorsFromGame(plugin, spectators);
        Player sender = Bukkit.getPlayer(senderUUID);
        Player target = Bukkit.getPlayer(targetUUID);
        GameUtils.resetShieldsDelay(plugin, restrictions, sender);
        GameUtils.resetShieldsDelay(plugin, restrictions, target);

        setIsInGame(false);
        setIsAboutToTeleportedToSpawn(true);
        setWinnerUUID(winnerUUIDFromMethod);
        request.storeRequest(false);

        Bukkit.getScheduler().cancelTask(MemoryStorage.tasksToCancel.get(senderUUID));
        MemoryStorage.tasksToCancel.remove(senderUUID);

        Player winner = Bukkit.getPlayer(winnerUUID);
        UUID loserUUID = getOpponent(winnerUUID);
        Player loser = Bukkit.getPlayer(getOpponent(winnerUUID));
        if (winner == null) {
            plugin.getLogger().log(Level.INFO, "&c&lDUELS SOMETHING WENT SUPER WRONG. CONTACT GREENED ERROR TYPE #3");
        }
        if (loser != null) {
            Chat.sendMessage(loser, plugin.languageConfig.getString("duel.game.lost-duel"));
        }
        winner.setHealth(20);
        Chat.sendMessage(winner, plugin.languageConfig.getString("duel.game.won-duel").replace("%number%", plugin.getConfig().getInt("game.time_to_pick_up_items") + ""));
        GameUtils.teleportToSpawn(plugin, loser);
        Duels.scheduler.runTaskLater(plugin, () -> {
            GameUtils.teleportToSpawn(plugin, winner);
            arena.setAvailable(true);
            GameUtils.reverseInventorySavingIfEnabled(restrictions, winner);
            request.removeStoreRequest(true);
            QueueSystem.checkQueue(plugin);
        }, 20L * plugin.getConfig().getInt("game.time_to_pick_up_items"));
        GameUtils.executeEndCommands(plugin, restrictions, winner, loser, arena.getID());
        if (bet != 0) {
            double tax = plugin.getConfig().getDouble("game.betting.tax-amount");
            BettingSystem.execGiveMoneyCommands(plugin, 2 * bet * (100 - tax) / 100, winner.getName());
            Chat.sendMessage(winner, plugin.languageConfig.getString("duel.betting.bet-added"));
        }
        MemoryStorage.playersWhoShouldBeTeleportedToSpawnAfterRespawn.add(loserUUID);
        GameEndEvent event = new GameEndEvent(this, winner, loser);
        Bukkit.getPluginManager().callEvent(event);
    }

    public UUID getOpponent(UUID someone) {
        if (someone == senderUUID) {
            return targetUUID;
        } else if (someone == targetUUID) {
            return senderUUID;
        } else {
            plugin.getLogger().log(Level.INFO, "Plugin tried to get opponent of the player that's " +
                    "not in that duel object. please report this to the author immediately");
            return null;
        }
    }

    public Instant getRunOutOfTimeInstant() {
        return runOutOfTime;
    }

    public void setRunOutOfTimeInstant(Instant newValue) {
        runOutOfTime = newValue;
    }

    public int getNumberOfSpectators() {
        return spectators.size();
    }

    public Arena getArena() {
        return arena;
    }

    public double getBet() {
        return bet;
    }

    public void saveAndRunRanOutOfTimeTask() {
        int max_duel_time_minutes = plugin.getConfig().getInt("game.max_duel_time");
        taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            endGameRanOutOfTime();
        }, 20L * 60 * max_duel_time_minutes).getTaskId();
        MemoryStorage.tasksToCancel.put(senderUUID, taskId);
        Instant timeWhenDuelRunsOutOfTime = Instant.now().plus(max_duel_time_minutes, ChronoUnit.MINUTES);
        setRunOutOfTimeInstant(timeWhenDuelRunsOutOfTime);
    }
}
