package net.multylands.duels.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.gui.DuelInventoryHolder;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.UUID;

public class GUI implements Listener {
    Duels plugin;

    public GUI(Duels plugin) {
        this.plugin = plugin;
    }

    public static HashSet<UUID> PlayersWhoSentRequest = new HashSet<>();

    @EventHandler
    public void onGuiClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getLocation() != null) {
            return;
        }
        if (!(inv.getHolder() instanceof DuelInventoryHolder)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (PlayersWhoSentRequest.contains(playerUUID)) {
            PlayersWhoSentRequest.remove(playerUUID);
            return;
        }
        DuelInventoryHolder invHolder = ((DuelInventoryHolder) inv.getHolder());
        DuelRequest request = invHolder.getRequest();
        request.removeStoreRequest(false);
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.cancel.request-cancelled"));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Inventory inv = event.getInventory();
        if (inv.getLocation() != null) {
            return;
        }
        if (!(inv.getHolder() instanceof DuelInventoryHolder)) {
            return;
        }
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);
        DuelInventoryHolder invHolder = ((DuelInventoryHolder) inv.getHolder());
        //always!!! get this request from the GUI clicker. because we are storing only sender: request in the requests map.
        DuelRequest request = invHolder.getRequest();
        Player target = Bukkit.getPlayer(request.getOpponent(player.getUniqueId()));
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            request.removeStoreRequest(false);
            player.closeInventory();
            return;
        }
        DuelRestrictions restrictions = request.getGame().getRestrictions();
        boolean isBowEnabled = restrictions.isBowAllowed();
        boolean isTotemEnabled = restrictions.isTotemsAllowed();
        boolean isGPEnabled = restrictions.isGoldenAppleAllowed();
        boolean isNotchEnabled = restrictions.isNotchAllowed();
        boolean isPotionsEnabled = restrictions.isPotionsAllowed();
        boolean isShieldsEnabled = restrictions.isShieldsAllowed();
        boolean isElytraEnabled = restrictions.isElytraAllowed();
        boolean isEnderPearlEnabled = restrictions.isEnderPearlAllowed();
        boolean isKeepInventoryEnabled = restrictions.isKeepInventoryAllowed();
        int bowSlot = plugin.languageConfig.getInt("duel-GUI.toggle-bow.slot");
        int totemSlot = plugin.languageConfig.getInt("duel-GUI.toggle-totem.slot");
        int GPSlot = plugin.languageConfig.getInt("duel-GUI.toggle-golden-apple.slot");
        int NotchSlot = plugin.languageConfig.getInt("duel-GUI.toggle-enchanted-golden-apple.slot");
        int potionsSlot = plugin.languageConfig.getInt("duel-GUI.toggle-potions.slot");
        int shieldsSlot = plugin.languageConfig.getInt("duel-GUI.toggle-shields.slot");
        int elytraSlot = plugin.languageConfig.getInt("duel-GUI.toggle-elytra.slot");
        int enderpearlSlot = plugin.languageConfig.getInt("duel-GUI.toggle-ender-pearl.slot");
        int keepInventorySlot = plugin.languageConfig.getInt("duel-GUI.toggle-keep-inventory.slot");
        int startSlot = plugin.languageConfig.getInt("duel-GUI.start.slot");
        int cancelSlot = plugin.languageConfig.getInt("duel-GUI.cancel.slot");
        ItemMeta meta = item.getItemMeta();
        int slot = event.getSlot();
        //not using switch because it does not support using not very reliable int getter from the config
        if (slot == bowSlot) {
            isBowEnabled = !isBowEnabled;
            restrictions.setBowAllowed(isBowEnabled);
            if (isBowEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == totemSlot) {
            isTotemEnabled = !isTotemEnabled;
            restrictions.setTotemsAllowed(isTotemEnabled);
            if (isTotemEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == GPSlot) {
            isGPEnabled = !isGPEnabled;
            restrictions.setGoldenAppleAllowed(isGPEnabled);
            if (isGPEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == NotchSlot) {
            isNotchEnabled = !isNotchEnabled;
            restrictions.setNotchAllowed(isNotchEnabled);
            if (isNotchEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == potionsSlot) {
            isPotionsEnabled = !isPotionsEnabled;
            restrictions.setPotionsAllowed(isPotionsEnabled);
            if (isPotionsEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-potions.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-potions.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == shieldsSlot) {
            isShieldsEnabled = !isShieldsEnabled;
            restrictions.setShieldsAllowed(isShieldsEnabled);
            if (isShieldsEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-shields.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-shields.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == elytraSlot) {
            isElytraEnabled = !isElytraEnabled;
            restrictions.setElytraAllowed(isElytraEnabled);
            if (isElytraEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-elytra.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-elytra.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == enderpearlSlot) {
            isEnderPearlEnabled = !isEnderPearlEnabled;
            restrictions.setEnderPearlAllowed(isEnderPearlEnabled);
            if (isEnderPearlEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-ender-pearl.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-ender-pearl.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == startSlot) {
            restrictions.setComplete(true);
            //dont change position of player and target below
            request = RequestUtils.getRequestForCommands(target.getUniqueId(), player.getUniqueId());
            request.getGame().setDuelRestrictions(restrictions);
//          request = new DuelRequest(player.getUniqueId(), target.getUniqueId(), restrictions, false, false, plugin);


            request.storeRequest(false);
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.request-sent").replace("%player%", target.getName()));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.duel.request-received").replace("%player%", player.getName()));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.restrictions.restrictions"));
            if (request.getGame().getRestrictions().getEnabled() != null) {
                Chat.sendMessage(target, plugin.languageConfig.getString("duel.restrictions.enabled-restrictions") + request.getGame().getRestrictions().getEnabled());
            }
            if (request.getGame().getRestrictions().getDisabled() != null) {
                Chat.sendMessage(target, plugin.languageConfig.getString("duel.restrictions.disabled-restrictions") + request.getGame().getRestrictions().getDisabled());
            }
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.duel.click").replace("%player%", player.getName()));
            PlayersWhoSentRequest.add(player.getUniqueId());
            player.closeInventory();
        } else if (slot == cancelSlot) {
            player.closeInventory();
            request.removeStoreRequest(false);
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.cancel.request-cancelled"));
        } else if (keepInventorySlot == slot) {
            isKeepInventoryEnabled = !isKeepInventoryEnabled;
            restrictions.setKeepInventoryAllowed(isKeepInventoryEnabled);
            if (isKeepInventoryEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-keep-inventory.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-keep-inventory.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        }
    }
}
