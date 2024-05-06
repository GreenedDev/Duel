package net.multylands.duels.commands.admin.arena;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class DeleteArenaCommand implements CommandExecutor {
    public Duels plugin;

    public DeleteArenaCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("duels.admin.deletearena")) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("no-perm"));
            return false;
        }
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 1) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " deletearena arenaName");
            return false;
        }
        String arenaName = args[0];
        if (!plugin.arenasConfig.contains(arenaName)) {
            Chat.sendMessage(player, plugin.languageConfig.getString("admin.delete-arena.doesnt-exists"));
            return false;
        }
        plugin.arenasConfig.set(arenaName, null);
        plugin.saveArenasConfig();
        plugin.reloadArenaConfig();
        //to prevent players getting lost when their arena was deleted.
        for (Set<DuelRequest> requestsSet : Duels.requestsReceiverToSenders.values()) {
            for (DuelRequest request : requestsSet) {
                if (!request.getGame().getIsInGame()) {
                    continue;
                }
                if (!request.getGame().getArena().getID().equals(arenaName)) {
                    continue;
                }
                request.getGame().endGameRestart();
            }
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("admin.delete-arena.success").replace("%arena%", arenaName));
        return false;
    }
}
