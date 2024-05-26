package net.multylands.duels.commands.player.request;


import net.multylands.duels.Duels;
import net.multylands.duels.gui.GUIManager;
import net.multylands.duels.listeners.Restrictions;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SendCommand implements CommandExecutor, TabCompleter {
    public GUIManager guiManager;
    public Duels plugin;

    public SendCommand(GUIManager guimanager, Duels plugin) {
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
            for (DuelRequest request : RequestUtils.getPlayerRequestsR_S(target)) {
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
        //target already in duel check
        for (DuelRequest request : RequestUtils.getPlayerRequestsR_S(target)) {
            if (!request.getGame().getIsInGame()) {
                continue;
            }
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.target-already-in-duel").replace("%player%", target.getDisplayName()));
            return true;
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
            DuelRequest oldRequest = MemoryStorage.inventoryRequests.get(player.getUniqueId());
            if (oldRequest == null) {
                guiManager.openDuelInventory(player, target, bet, getDefaultRestrictions(plugin));
            } else {

                guiManager.openDuelInventory(player, target, bet, oldRequest.getGame().getRestrictions());
            }
        } else {

            DuelRequest request = new DuelRequest(player.getUniqueId(), target.getUniqueId(), getDefaultRestrictions(plugin), false, false, bet, plugin);
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabCompleteStrings = new ArrayList<>();
        Bukkit.getOnlinePlayers().stream().forEach(player -> {
            String playerName = player.getName().toLowerCase();
            String args0 = args[0].toLowerCase();
            if (playerName.startsWith(args0) && player.getName() != sender.getName()) {
                tabCompleteStrings.add(player.getName());
            }
        });
        return tabCompleteStrings;
    }
    public static DuelRestrictions getDefaultRestrictions(Duels plugin) {

        boolean bowAllowed = getRestrictionToggledByDefault(plugin, "bow");
        boolean notchAllowed = getRestrictionToggledByDefault(plugin, "enchanted-golden-apple");
        boolean potionAllowed = getRestrictionToggledByDefault(plugin, "potion");
        boolean goldenAppleAllowed = getRestrictionToggledByDefault(plugin, "golden-apple");
        boolean shieldAllowed = getRestrictionToggledByDefault(plugin, "shield");
        boolean totemAllowed = getRestrictionToggledByDefault(plugin, "totem");
        boolean elytraAllowed = getRestrictionToggledByDefault(plugin, "elytra");
        boolean enderPearlAllowed = getRestrictionToggledByDefault(plugin, "ender-pearl");
        boolean keep_inventory_enabled = getModuleToggledByDefault(plugin, "keep-inventory");
        boolean inventory_saving_enabled = getModuleToggledByDefault(plugin, "inventory-saving");
        return new DuelRestrictions(bowAllowed, notchAllowed, potionAllowed, goldenAppleAllowed, shieldAllowed, totemAllowed, elytraAllowed, enderPearlAllowed, true, keep_inventory_enabled, inventory_saving_enabled);
    }
    public static boolean getRestrictionToggledByDefault(Duels plugin, String name) {
        return plugin.getConfig().getBoolean("modules.restrictions."+name+".toggled-by-default");
    }
    public static boolean getModuleToggledByDefault(Duels plugin, String name) {
        return plugin.getConfig().getBoolean("modules."+name+".toggled-by-default");
    }
}
