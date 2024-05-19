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

import java.util.Objects;

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
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " player");
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            return false;
        }
        if (player.equals(target)) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.cant-duel-yourself"));
            return false;
        }
        //already sent check
        if (Duels.requestsReceiverToSenders.containsKey(target.getUniqueId())) {
            for (DuelRequest request : Duels.requestsReceiverToSenders.get(target.getUniqueId())) {
                if (request.getSender() != player.getUniqueId()) {
                    continue;
                }
                if (request.getGame().getIsAboutToTeleportedToSpawn()) {
                    Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.target-already-in-duel").replace("%player%", target.getDisplayName()));
                    return false;
                }
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.request-already-sent").replace("%player%", target.getDisplayName()));
                return false;
            }
        }
        //target already in duel check
        if (Duels.requestsReceiverToSenders.containsKey(target.getUniqueId())) {
            for (DuelRequest request : Duels.requestsReceiverToSenders.get(target.getUniqueId())) {
                if (!request.getGame().getIsInGame()) {
                    continue;
                }
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.target-already-in-duel").replace("%player%", target.getDisplayName()));
                return false;
            }
        }
        //ignore check
        if (plugin.ignoresConfig.contains("Ignores." + target.getUniqueId())) {
            for (String loopUUID : plugin.ignoresConfig.getStringList("Ignores." + target.getUniqueId())) {
                if (!Objects.equals(loopUUID, player.getUniqueId().toString())) {
                    continue;
                }
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.ignore.player-is-ignoring-requests"));
                return false;
            }
        }
        if (plugin.getConfig().getBoolean("modules.GUI")) {
            guiManager.openInventory(player, target);
        } else {
            DuelRestrictions restrictions = new DuelRestrictions(true, true, true, true, true, true, true, true, true, false, false);
            DuelRequest request = new DuelRequest(player.getUniqueId(), target.getUniqueId(), restrictions, false, false, plugin);
            request.storeRequest(false);
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.request-sent").replace("%player%", target.getName()));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.duel.request-received").replace("%player%", player.getName()));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.duel.click").replace("%player%", player.getName()));
        }
        return false;
    }
}
