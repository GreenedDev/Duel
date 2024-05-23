package net.multylands.duels.commands.player.request;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.*;
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
            return true;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 1) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " player");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            return true;
        }
        for (Arena arena : MemoryStorage.Arenas.values()) {
            if (arena.isAvailable()) {
                continue;
            }
            if (player.getUniqueId().equals(arena.getSenderUUID()) || player.getUniqueId().equals(arena.getTargetUUID())) {
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.already-in-duel"));
                return true;
            }
        }
        DuelRequest request = RequestUtils.getRequestForCommands(player.getUniqueId(), target.getUniqueId());

        if (request == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.target-hasnt-sent-request"));
            return true;
        }
        Arena availableArena;
        if (MemoryStorage.selectedArenas.get(target.getUniqueId()) == null) {
            availableArena = ArenaUtils.getAvailableArena();
        } else {
            availableArena = MemoryStorage.selectedArenas.get(target.getUniqueId());
        }
        if (availableArena == null || !availableArena.isAvailable()) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.no-arenas-available"));
            return true;
        }
        if (!request.getGame().getRestrictions().isComplete()) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.target-hasnt-sent-request"));
            return true;
        }
        double bet = request.getGame().getBet();
        if (bet != 0) {
            if (!BettingSystem.econ.has(player, bet)) {
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.betting.insufficient-funds"));
                return true;
            }
            if (!BettingSystem.econ.has(target, bet)) {
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.betting.insufficient-funds-sender"));
                return true;
            }
            BettingSystem.execTakeMoneyCommands(plugin, bet, player.getName());
            BettingSystem.execTakeMoneyCommands(plugin, bet, target.getName());
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.betting.bet-removed"));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.betting.bet-removed"));
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.you-accepted-request").replace("%player%", target.getDisplayName()));
        Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.accept.request-accepted").replace("%player%", player.getDisplayName()));
        request.getGame().start(availableArena);
        return true;
    }
}
