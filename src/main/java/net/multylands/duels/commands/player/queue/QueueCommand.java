package net.multylands.duels.commands.player.queue;

import net.multylands.duels.Duels;
import net.multylands.duels.commands.player.CheckPermissions;
import net.multylands.duels.queue.QueueSystem;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


public class QueueCommand implements CommandExecutor {
    public Duels plugin;

    public QueueCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return true;
        }
        Player player = ((Player) sender).getPlayer();
        if (!CheckPermissions.hasPermission(plugin.getConfig(), "queue", "duel.commands.queue", player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("no-perm"));
            return true;
        }
        if (args.length != 0) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " queue");
            return true;
        }
        UUID playerUUID = player.getUniqueId();
        if (QueueSystem.playersInQueue.contains(playerUUID)) {
            QueueSystem.playersInQueue.remove(playerUUID);
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.queue.removed"));
        } else {
            QueueSystem.playersInQueue.add(playerUUID);
            QueueSystem.checkQueue(plugin);
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.queue.added"));
        }
        return true;
    }
}
