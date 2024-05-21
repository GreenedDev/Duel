package net.multylands.duels.commands;


import net.multylands.duels.Duels;
import net.multylands.duels.gui.GUIManager;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {
    public GUIManager guiManager;
    public Duels plugin;

    public DuelCommand(GUIManager guimanager, Duels plugin) {
        this.guiManager = guimanager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return true;
        }
        Player player = (Player) sender;
        if (!(args.length == 1 || args.length == 2)) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " player bet(optional)");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            return true;
        }
        if (player.equals(target)) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.cant-duel-yourself"));
            return true;
        }
        //already sent check
        if (Duels.requestsReceiverToSenders.containsKey(target.getUniqueId())) {
            for (DuelRequest request : Duels.requestsReceiverToSenders.get(target.getUniqueId())) {
                if (request.getSender() != player.getUniqueId()) {
                    continue;
                }
                if (request.getGame().getIsAboutToTeleportedToSpawn()) {
                    Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.target-already-in-duel").replace("%player%", target.getDisplayName()));
                    return true;
                }
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.request-already-sent").replace("%player%", target.getDisplayName()));
                return true;
            }
        }
        //target already in duel check
        if (Duels.requestsReceiverToSenders.containsKey(target.getUniqueId())) {
            for (DuelRequest request : Duels.requestsReceiverToSenders.get(target.getUniqueId())) {
                if (!request.getGame().getIsInGame()) {
                    continue;
                }
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.target-already-in-duel").replace("%player%", target.getDisplayName()));
                return true;
            }
        }
        //ignore check
        if (plugin.ignoresConfig.getStringList(target.getUniqueId().toString()).contains(player.getUniqueId().toString())) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.ignore.player-is-ignoring-requests"));
            return true;
        }
        float bet = 0;
        if (args.length == 2) {
            if (!plugin.getConfig().getBoolean("game.betting.enabled")) {
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.betting.not-enabled"));
                return true;
            }
            try {
                bet = Float.parseFloat(args[1]);
            } catch (NumberFormatException e) {
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.betting.only-number"));
                return true;
            }
            double minimum = plugin.getConfig().getDouble("game.betting.minimum");
            double maximum = plugin.getConfig().getDouble("game.betting.maximum");
            if (bet < minimum || bet > maximum) {
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.betting.out-of-range")
                        .replace("%minimum%", String.valueOf(minimum))
                        .replace("%maximum%", String.valueOf(maximum)));
                return true;
            }
        }
        if (plugin.getConfig().getBoolean("modules.GUI")) {
            DuelRestrictions restrictions = new DuelRestrictions(true, true, true, true, true, true, true, true, false, false, false);
            guiManager.openDuelInventory(player, target, bet, restrictions);
        } else {
            DuelRestrictions restrictions = new DuelRestrictions(true, true, true, true, true, true, true, true, true, false, false);
            DuelRequest request = new DuelRequest(player.getUniqueId(), target.getUniqueId(), restrictions, false, false, bet, plugin);
            request.storeRequest(false);
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.request-sent").replace("%player%", target.getName()));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.duel.request-received").replace("%player%", player.getName()));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.duel.click").replace("%player%", player.getName()));
            if (plugin.getConfig().getBoolean("game.betting.enabled") && bet != 0) {
                Chat.sendMessage(target, plugin.languageConfig.getString("duel.betting.bet-amount").replace("%amount%", String.valueOf(bet)));
            }
        }
        return true;
    }
}
