package net.multylands.duels.commands.player.request;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelCommand implements CommandExecutor {
    Duels plugin;

    public CancelCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 1) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " cancel player");
            return false;
        }
        //checking if he has sent any request
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            return false;
        }
        DuelRequest request = RequestUtils.getRequestForCommands(target.getUniqueId(), player.getUniqueId());
        if (request == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.no-request-sent").replace("%player%", target.getName()));
            return false;
        }
        if (request.getGame().getIsAboutToTeleportedToSpawn()) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.no-request-sent").replace("%player%", target.getName()));
            return false;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.cancel.request-cancelled"));
        Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.cancel.someone-cancelled-request").replace("%player%", player.getName()));
        request.removeStoreRequest(false);
        return false;
    }
}
