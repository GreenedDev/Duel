package net.multylands.duels.placeholders;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.queue.QueueSystem;
import net.multylands.duels.utils.storage.MemoryStorage;
import net.multylands.duels.utils.RequestUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class CalculatePlaceholders {
    public static String getOpponent(OfflinePlayer player) {
        if (!player.isOnline()) {
            return "Error #1";
        }
        UUID playerUUID = player.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return "You aren't in the duel";
        }
        UUID opponentUUID = request.getOpponent(playerUUID);
        Player opponent = Bukkit.getPlayer(opponentUUID);
        if (opponent == null) {
            return "opponent isn't online";
        }
        return opponent.getName();
    }
    public static String getBet(OfflinePlayer player) {
        if (!player.isOnline()) {
            return "Error #1";
        }
        UUID playerUUID = player.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return "You aren't in the duel";
        }
        return request.getGame().getBet()+"";
    }

    public static String getOpponentPing(OfflinePlayer player) {
        if (!player.isOnline()) {
            return "Error #1";
        }
        UUID playerUUID = player.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return "You aren't in the duel";
        }
        UUID opponentUUID = request.getOpponent(playerUUID);
        Player opponent = Bukkit.getPlayer(opponentUUID);
        if (opponent == null) {
            return "opponent isn't online";
        }
        return String.valueOf(opponent.getPing());
    }

    public static String getNumberOfSpectators(OfflinePlayer player) {
        if (!player.isOnline()) {
            return "Error #1";
        }
        UUID playerUUID = player.getUniqueId();

        UUID toSpectate = MemoryStorage.spectators.get(playerUUID);
        if (toSpectate == null) {
            DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
            if (!RequestUtils.isInGame(request)) {
                return "You aren't in a duel or spectating someone's duel";
            }
            return String.valueOf(request.getGame().getNumberOfSpectators());
        }
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(toSpectate);
        if (!RequestUtils.isInGame(request)) {
            return "number of spectators error #1";
        }
        return String.valueOf(request.getGame().getNumberOfSpectators());
    }
    public static String getNumberOfQueuePlayers() {
        return String.valueOf(QueueSystem.playersInQueue.size());
    }

    public static String getTimeLeft(OfflinePlayer player, Duels plugin) {
        if (!player.isOnline()) {
            return "Error #1";
        }
        UUID playerUUID = player.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return "You aren't in the duel";
        }
        Instant timeWhenDuelRunsOutOfTime = request.getGame().getRunOutOfTimeInstant();
        Instant now = Instant.now();

        long secondsBetween = now.until(timeWhenDuelRunsOutOfTime, ChronoUnit.SECONDS);
        return convertSecondsToHMS(secondsBetween, plugin);
    }

    public static String convertSecondsToHMS(long seconds, Duels plugin) {
        long H = (seconds / 60) / 60;  // covert total seconds to hours
        long M = (seconds / 60) % 60;  // Calculate the remaining minutes
        long S = seconds % 60;         // Calculate the remaining seconds
        String result = "";
        String hoursDisplayed = plugin.getConfig().getString("placeholders.time.hours");
        String minutesDisplayed = plugin.getConfig().getString("placeholders.time.minutes");
        String secondsDisplayed = plugin.getConfig().getString("placeholders.time.seconds");
        if (H != 0) {
            result = H + " " + hoursDisplayed + " ";
        }
        if (M != 0) {
            result = result + M + " " + minutesDisplayed + " ";
        }
        if (S != 0) {
            result = result + S + " " + secondsDisplayed;
        }
        return result;
    }
}
