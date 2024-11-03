package net.multylands.duels.commands.player.ignore;

import net.multylands.duels.Duels;
import net.multylands.duels.commands.player.CheckPermissions;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IgnoreCommand implements CommandExecutor {
    public Duels plugin;

    public IgnoreCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (!CheckPermissions.hasPermission(plugin.getConfig(), "ignore", "duel.commands.ignore", player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("no-perm"));
            return true;
        }
        if (args.length != 1) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " ignore player");
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            return false;
        }
        String playerUUID = player.getUniqueId().toString();
        String targetUUID = target.getUniqueId().toString();
        if (!plugin.ignoresConfig.getKeys(false).contains(playerUUID)) {
            List<String> uuids = new ArrayList<>(Collections.emptyList());
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.ignore.ignoring-on-player-enable").replace("%player%", target.getName()));
            uuids.add(targetUUID);
            plugin.ignoresConfig.set(playerUUID, uuids);
            plugin.saveIgnoresConfig();
            return false;
        }
        List<String> uuids = plugin.ignoresConfig.getStringList(playerUUID);
        if (uuids.contains(targetUUID)) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.ignore.ignoring-on-player-disable").replace("%player%", target.getName()));
            uuids.remove(targetUUID);
            plugin.ignoresConfig.set(playerUUID, null);
        } else {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.ignore.ignoring-on-player-enable").replace("%player%", target.getName()));
            uuids.add(targetUUID);
            plugin.ignoresConfig.set(playerUUID, uuids);
        }
        plugin.saveIgnoresConfig();
        return false;
    }
}
