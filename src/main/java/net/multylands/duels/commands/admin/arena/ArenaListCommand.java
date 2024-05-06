package net.multylands.duels.commands.admin.arena;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaListCommand implements CommandExecutor {
    public Duels plugin;

    public ArenaListCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("duels.admin.arenalist")) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("no-perm"));
            return false;
        }
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 0) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label));
            return false;
        }
        if (plugin.arenasConfig.getKeys(false).isEmpty()) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.no-arenas-available"));
            return false;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("admin.arena-list.meaning"));
        Chat.sendMessage(player, plugin.languageConfig.getString("admin.arena-list.list"));
        String format = "<gray>(</gray><color:#00f299><click:run_command:'/dueladmin setarenapos %arena% pos1'><hover:show_text:'<blue>Click to set pos1!</blue>'>Set Pos1</hover></click></color><gray>)</gray> <gray>(</gray><color:#00f299><click:run_command:'/dueladmin setarenapos %arena% pos2'><hover:show_text:'<color:#cc00cc>Click to set pos2!</color>'>Set Pos2</hover></click></color><gray>)</gray> <gray>(</gray><color:#ff1900><click:run_command:'/dueladmin deletearena %arena%'><hover:show_text:'<aqua>Click to delete!</aqua>'>Delete</hover></click></color><gray>)</gray>";
        for (String arenaName : plugin.arenasConfig.getKeys(false)) {
            if (plugin.arenasConfig.getLocation(arenaName + ".pos1") == null
                    || plugin.arenasConfig.getLocation(arenaName + ".pos2") == null) {
                Chat.sendMessage(player, "$<gray>-</gray> <red>" + arenaName + "</red> " + format.replace("%arena%", arenaName));
                continue;
            }
            Chat.sendMessage(player, "$<gray>-</gray> <green>" + arenaName + "</green> " + format.replace("%arena%", arenaName));
        }
        return false;
    }
}
