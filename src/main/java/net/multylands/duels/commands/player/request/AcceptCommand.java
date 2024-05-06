package net.multylands.duels.commands.player.request;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class AcceptCommand implements CommandExecutor {
    public Duels plugin;

    public AcceptCommand(Duels plugin) {
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
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " player");
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            return false;
        }
        for (Arena arena : Duels.Arenas.values()) {
            if (arena.isAvailable()) {
                continue;
            }
            if (player.getUniqueId().equals(arena.getSenderUUID()) || player.getUniqueId().equals(arena.getTargetUUID())) {
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.already-in-duel"));
                return false;
            }
        }
        DuelRequest request = RequestUtils.getRequestForCommands(player.getUniqueId(), target.getUniqueId());

        if (request == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.target-hasnt-sent-request"));
            return false;
        }
        boolean Available = false;
        Arena availableArena = null;
        for (Arena arena : Duels.Arenas.values()) {
            if (!arena.isAvailable()) {
                continue;
            }
            Available = true;
            availableArena = arena;
            break;
        }
        if (!Available) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.no-arenas-available"));
            return false;
        }

        if (!request.getGame().getRestrictions().isComplete()) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.target-hasnt-sent-request"));
            return false;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.you-accepted-request").replace("%player%", target.getDisplayName()));
        Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.accept.request-accepted").replace("%player%", player.getDisplayName()));
        request.getGame().start(availableArena);
        return false;
    }
}
