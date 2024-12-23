package net.multylands.duels.commands.player.request;

import net.multylands.duels.Duels;
import net.multylands.duels.commands.player.CheckPermissions;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.ArenaUtils;
import net.multylands.duels.utils.BettingSystem;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


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
        if (!CheckPermissions.hasPermission(plugin.getConfig(), "accept", "duel.commands.accept", player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("no-perm"));
            return true;
        }
        UUID playerUUID = player.getUniqueId();
        if (args.length != 1) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " accept player");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            return true;
        }
        UUID targetUUID = target.getUniqueId();
        for (Arena arena : MemoryStorage.Arenas.values()) {
            if (arena.isAvailable()) {
                continue;
            }
            if (targetUUID.equals(arena.getSenderUUID()) || targetUUID.equals(arena.getTargetUUID())) {
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.already-in-duel"));
                return true;
            }
        }
        DuelRequest request = RequestUtils.getRequestForCommands(playerUUID, targetUUID);

        if (request == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.target-hasnt-sent-request"));
            return true;
        }
        Arena availableArena;
        if (MemoryStorage.selectedArenas.get(targetUUID) == null) {
            availableArena = ArenaUtils.getAvailableArena();
        } else {
            availableArena = MemoryStorage.selectedArenas.get(targetUUID);
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
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.accept.you-accepted-request").replace("%player%", target.getName()));
        Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.accept.request-accepted").replace("%player%", player.getName()));
        //If player has sent the request to same player but the same player sent
        //another request to this player, and he accepted, we are removing first request
        if (RequestUtils.getRequestForCommands(targetUUID, playerUUID) != null) {
            DuelRequest oldRequestOfFirstPlayer = RequestUtils.getRequestForCommands(targetUUID, playerUUID);
            oldRequestOfFirstPlayer.removeStoreRequest(false);
        }

        request.getGame().start(availableArena);
        return true;
    }
}
