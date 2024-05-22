package net.multylands.duels.queue;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QueueListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!QueueSystem.playersInQueue.contains(playerUUID)) {
            return;
        }
        QueueSystem.playersInQueue.remove(playerUUID);
    }
}
