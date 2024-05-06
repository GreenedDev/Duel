package net.multylands.duels.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener implements Listener {
    Duels plugin;

    public UpdateListener(Duels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) {
            return;
        }
        if (plugin.newVersion == null) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("update-available").replace("%newversion%", plugin.newVersion));
    }
}
