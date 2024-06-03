package net.multylands.duels.commands.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CheckPermissions {
    public static boolean hasPermission(ConfigurationSection config, String name, String perm, Player player) {
        if (!config.getBoolean("permissions." + name)) {
            return true;
        }
        if (player.hasPermission(perm)) {
            return true;
        }
        return false;
    }
}
