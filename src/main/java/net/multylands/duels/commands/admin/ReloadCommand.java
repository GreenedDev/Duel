package net.multylands.duels.commands.admin;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    Duels plugin;

    public ReloadCommand(Duels duels) {
        plugin = duels;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("duels.admin.reload")) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("no-perm"));
            return false;
        }
        if (args.length != 0) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " reload");
            return false;
        }
        plugin.reloadArenaConfig();
        plugin.reloadConfig();
        plugin.reloadLanguageConfig();
        Chat.sendMessageSender(sender, plugin.languageConfig.getString("admin.reload.all-config-reloaded"));
        return false;
    }
}
