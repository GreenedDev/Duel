package net.multylands.duels.commands.admin.arena;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateArenaCommand implements CommandExecutor {
    public Duels plugin;

    public CreateArenaCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("duels.admin.createarena")) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("no-perm"));
            return false;
        }
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 1) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " createarena arenaName");
            return false;
        }
        String arenaName = args[0];
        if (plugin.arenasConfig.contains(arenaName)) {
            Chat.sendMessage(player, plugin.languageConfig.getString("admin.create-arena.already-exists"));
            return false;
        }
        plugin.arenasConfig.set(arenaName + ".isnew", true);
        plugin.saveArenasConfig();
        Chat.sendMessage(player, plugin.languageConfig.getString("admin.create-arena.success").replace("%arena%", arenaName));
        return false;
    }
}
