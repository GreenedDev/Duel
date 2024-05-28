package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpectatorUtils {
    public static void endSpectating(Player player, Duels plugin) {
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        UUID playerUUID = player.getUniqueId();
        UUID toSpectateUUID = MemoryStorage.spectators.get(playerUUID);

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(toSpectateUUID);
        MemoryStorage.spectators.remove(playerUUID);
        Player firstPlayer = Bukkit.getPlayer(request.getTarget());
        Player opponent = Bukkit.getPlayer(request.getSender());

        player.teleport(spawnLoc);
        player.setAllowFlight(false);
        if (firstPlayer != null) {
            firstPlayer.showPlayer(plugin, player);
        }
        if (opponent != null) {
            opponent.showPlayer(plugin, player);
        }
        request.getGame().removeSpectator(playerUUID);
        request.storeRequest(false);
    }

    public static void endSpectatingForEndGame(Player player, Duels plugin) {
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        UUID playerUUID = player.getUniqueId();
        UUID toSpectateUUID = MemoryStorage.spectators.get(playerUUID);

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(toSpectateUUID);
        MemoryStorage.spectators.remove(playerUUID);
        Player firstPlayer = Bukkit.getPlayer(request.getTarget());
        Player opponent = Bukkit.getPlayer(request.getSender());
        player.teleport(spawnLoc);
        player.setAllowFlight(false);
        if (firstPlayer != null) {
            firstPlayer.showPlayer(plugin, player);
        }
        if (opponent != null) {
            opponent.showPlayer(plugin, player);
        }
    }

    public static void startSpectating(Player player, Player toSpectate, Duels plugin) {
        UUID toSpectateUUID = toSpectate.getUniqueId();
        UUID playerUUID = player.getUniqueId();

        //the teleport needs to be first here
        player.teleport(toSpectate);
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(toSpectateUUID);
        Player opponent = Bukkit.getPlayer(request.getOpponent(toSpectateUUID));
        MemoryStorage.spectators.put(playerUUID, toSpectateUUID);
        player.setAllowFlight(true);
        toSpectate.hidePlayer(plugin, player);
        opponent.hidePlayer(plugin, player);
        request.getGame().addSpectator(playerUUID);
        request.storeRequest(false);
    }
}
