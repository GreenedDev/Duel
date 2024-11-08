package net.multylands.duels.commands.player.request;

import net.multylands.duels.Duels;
import net.multylands.duels.commands.player.CheckPermissions;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DenyCommand implements CommandExecutor {
    Duels plugin;

    public DenyCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (!CheckPermissions.hasPermission(plugin.getConfig(), "deny", "duel.commands.deny", player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("no-perm"));
            return true;
        }
        UUID playerUUID = player.getUniqueId();
        if (args.length != 1) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " deny player");
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            return false;
        }
        UUID targetUUID = target.getUniqueId();
        DuelRequest request = RequestUtils.getRequestForCommands(playerUUID, targetUUID);
        if (request == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.deny.target-hasnt-sent-request"));
            return false;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.deny.you-denied-request").replace("%player%", target.getName()));
        Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.deny.someone-denied-your-request").replace("%player%", player.getName()));
        request.removeStoreRequest(false);
        return false;
    }
}
