package net.multylands.duels.object;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.SavingItems;
import net.multylands.duels.utils.SpectatorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class Game {
    UUID senderUUID;
    UUID targetUUID;

    HashMap<Integer, ItemStack> senderInvItems = new HashMap<>();
    HashMap<Integer, ItemStack> targetInvItems = new HashMap<>();

    UUID winnerUUID;
    DuelRestrictions duelRestrictions;
    boolean isInGame;
    int taskAssignedIDInTheList;
    boolean isAboutToBeTeleportedToSpawn = false;
    DuelRequest request;
    Duels plugin;
    List<UUID> spectators = new ArrayList<>();
    boolean isStartingIn5Seconds;
    Arena arena;
    int taskId = 0;
    Instant runOutOfTime;

    public Game(UUID sender, UUID target, DuelRequest request, DuelRestrictions duelRestrictions, boolean isInGame, boolean isStartingIn5Seconds, Duels plugin) {
        this.senderUUID = sender;
        this.targetUUID = target;
        this.request = request;
        this.isStartingIn5Seconds = isStartingIn5Seconds;
        this.duelRestrictions = duelRestrictions;
        this.isInGame = isInGame;
        this.plugin = plugin;
    }

    public UUID getSender() {
        return senderUUID;
    }

    public UUID getTarget() {
        return targetUUID;
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
        return duelRestrictions;
    }

    public void setSender(UUID player) {
        this.senderUUID = player;
    }

    public void setIsInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }

    public void setTarget(UUID target) {
        this.targetUUID = target;
    }

    public void setIsStartingIn5Seconds(boolean YesOrNot) {
        this.isStartingIn5Seconds = YesOrNot;
    }

    public void setDuelRestrictions(DuelRestrictions duelRestrictions) {
        this.duelRestrictions = duelRestrictions;
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

        SavingItems.saveAndClearInventoryIfEnabled(plugin, senderPlayer);
        SavingItems.saveAndClearInventoryIfEnabled(plugin, targetPlayer);

        targetPlayer.teleport(arena.getFirstLocation(plugin));
        senderPlayer.teleport(arena.getSecondLocation(plugin));

//        Set<DuelRequest> requestsThatWereAlreadyThere = RequestUtils.getRequestsReceiverToSenders(targetUUID, senderUUID);
//        requestsThatWereAlreadyThere.add(request);
//        Duels.requestsReceiverToSenders.put(senderUUID, requestsThatWereAlreadyThere);

        disableFlying(senderPlayer, targetPlayer);
        removeEffectsIfDisabled(senderPlayer, targetPlayer);
        disableShieldsIfDisabled(senderPlayer, targetPlayer);
        saveAndRunRanOutOfTimeTask();
        executeStartCommands(senderPlayer, targetPlayer);
        startCounting(senderPlayer, targetPlayer);

        request.storeRequest(true);
    }

    public void endGameRanOutOfTime() {
        removeSpectatorsFromGame();
        Player sender = Bukkit.getPlayer(senderUUID);
        Player target = Bukkit.getPlayer(targetUUID);
        resetShieldsDelay(sender);
        resetShieldsDelay(target);


        Chat.messagePlayers(sender, target, plugin.languageConfig.getString("duel.game.ran-out-of-time"));
        teleportToSpawn(sender);
        teleportToSpawn(target);
        request.removeStoreRequest(true);
        SavingItems.clearInvAndGiveItemsBackIfEnabled(plugin, sender);
        SavingItems.clearInvAndGiveItemsBackIfEnabled(plugin, target);
    }

    public void endGameRestart() {
        removeSpectatorsFromGame();
        Player sender = Bukkit.getPlayer(senderUUID);
        Player target = Bukkit.getPlayer(targetUUID);
        resetShieldsDelay(sender);
        resetShieldsDelay(target);


        Bukkit.getScheduler().cancelTask(Duels.tasksToCancel.get(senderUUID));
        Duels.tasksToCancel.remove(senderUUID);

        teleportToSpawn(sender);
        teleportToSpawn(target);
        request.removeStoreRequest(true);
        SavingItems.clearInvAndGiveItemsBackIfEnabled(plugin, sender);
        SavingItems.clearInvAndGiveItemsBackIfEnabled(plugin, target);
    }

    public void endGame(UUID winnerUUIDFromMethod) {
        removeSpectatorsFromGame();
        Player sender = Bukkit.getPlayer(senderUUID);
        Player target = Bukkit.getPlayer(targetUUID);
        resetShieldsDelay(sender);
        resetShieldsDelay(target);

        setIsInGame(false);
        setIsAboutToTeleportedToSpawn(true);
        setWinnerUUID(winnerUUIDFromMethod);
        request.storeRequest(false);


        Bukkit.getScheduler().cancelTask(Duels.tasksToCancel.get(senderUUID));
        Duels.tasksToCancel.remove(senderUUID);


        Player winner = Bukkit.getPlayer(winnerUUID);
        Player loser = Bukkit.getPlayer(getOpponent(winnerUUID));
        if (winner == null) {
            plugin.getLogger().log(Level.INFO, "&c&lDUELS SOMETHING WENT SUPER WRONG. CONTACT GREENED ERROR TYPE #3");
        }
        if (loser != null) {
            Chat.sendMessage(loser, plugin.languageConfig.getString("duel.game.lost-duel"));
        }
        Chat.sendMessage(winner, plugin.languageConfig.getString("duel.game.won-duel").replace("%number%", plugin.getConfig().getInt("game.time_to_pick_up_items") + ""));
        Duels.scheduler.runTaskLater(plugin, () -> {
            teleportToSpawn(winner);
            arena.setAvailable(true);
            SavingItems.clearInvAndGiveItemsBackIfEnabled(plugin, winner);
            request.removeStoreRequest(true);
        }, 20L * plugin.getConfig().getInt("game.time_to_pick_up_items"));
        executeEndCommands(winner, loser);
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

    public void saveAndRunRanOutOfTimeTask() {
        Random random = new Random();
        int max_duel_time_minutes = plugin.getConfig().getInt("game.max_duel_time");
        taskAssignedIDInTheList = random.nextInt(999999);
        taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            endGameRanOutOfTime();
        }, 20L * 60 * max_duel_time_minutes).getTaskId();
        Duels.tasksToCancel.put(senderUUID, taskId);
        Instant timeWhenDuelRunsOutOfTime = Instant.now().plus(max_duel_time_minutes, ChronoUnit.MINUTES);
        setRunOutOfTimeInstant(timeWhenDuelRunsOutOfTime);
    }

    public void disableShieldsIfDisabled(Player sender, Player target) {
        if (duelRestrictions.isShieldsAllowed()) {
            return;
        }
        int maxDuelTimeInTicks = plugin.getConfig().getInt("game.max_duel_time") * 60 * 20;
        sender.setShieldBlockingDelay(maxDuelTimeInTicks);
        target.setShieldBlockingDelay(maxDuelTimeInTicks);
    }

    public void resetShieldsDelay(Player player) {
        if (player == null) {
            return;
        }
        if (duelRestrictions.isShieldsAllowed()) {
            return;
        }
        player.setShieldBlockingDelay(plugin.getConfig().getInt("default-shield-blocking-delay"));
    }

    public void removeEffectsIfDisabled(Player sender, Player target) {
        if (duelRestrictions.isPotionsAllowed()) {
            return;
        }
        for (PotionEffect effect : sender.getActivePotionEffects()) {
            sender.removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : target.getActivePotionEffects()) {
            target.removePotionEffect(effect.getType());
        }
    }

    public void disableFlying(Player sender, Player target) {
        sender.setFlying(false);
        sender.setAllowFlight(false);
        target.setFlying(false);
        target.setAllowFlight(false);
    }

    public void executeEndCommands(Player winner, Player loser) {
        for (String commandFromTheLoop : plugin.getConfig().getStringList("game.commands.end")) {
            String command = commandFromTheLoop.replace("%winner%", winner.getName())
                    .replace("%loser%", loser.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public void executeStartCommands(Player senderPlayer, Player targetPlayer) {
        for (String commandFromTheLoop : plugin.getConfig().getStringList("game.commands.start")) {
            String command = commandFromTheLoop.replace("%player1%", senderPlayer.getName())
                    .replace("%player2%", targetPlayer.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public void startCounting(Player senderPlayer, Player targetPlayer) {
        AtomicInteger countdown = new AtomicInteger(6);
        Duels.scheduler.runTaskTimer(plugin, task -> {
            String color = Chat.getColorForNumber(countdown);
            countdown.getAndDecrement();
            if (countdown.get() == 0) {
                setIsStartingIn5Seconds(false);
                task.cancel();
                Chat.messagePlayers(senderPlayer, targetPlayer, plugin.languageConfig.getString("duel.game.duel-started"));
            } else {
                Chat.messagePlayers(senderPlayer, targetPlayer, plugin.languageConfig.getString("duel.game.duel-countdown").replace("%color+countdown%", color + countdown));
            }
        }, 0, 20);
    }

    public void removeSpectatorsFromGame() {
        for (UUID spectatorUUID : spectators) {
            SpectatorUtils.endSpectatingForEndGame(Bukkit.getPlayer(spectatorUUID), plugin);
        }
        spectators.clear();
    }

    public void teleportToSpawn(Player player) {
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        player.teleport(spawnLoc);
    }

}
