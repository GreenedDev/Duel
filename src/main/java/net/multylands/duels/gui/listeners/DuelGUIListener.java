package net.multylands.duels.gui.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.storage.MemoryStorage;
import net.multylands.duels.utils.RequestUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.UUID;

public class DuelGUIListener implements Listener {
    Duels plugin;

    public DuelGUIListener(Duels plugin) {
        this.plugin = plugin;
    }
    public static HashSet<UUID> playersWhoClosedBecauseOfArenaSelector = new HashSet<>();

    public static HashSet<UUID> PlayersWhoSentRequest = new HashSet<>();
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        MemoryStorage.duelInventories.remove(playerUUID);
        MemoryStorage.selectedArenas.remove(playerUUID);
        MemoryStorage.arenaInventories.remove(playerUUID);
        MemoryStorage.inventoryRequests.remove(playerUUID);
    }
    @EventHandler
    public void onGuiClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (playersWhoClosedBecauseOfArenaSelector.contains(playerUUID)) {
            playersWhoClosedBecauseOfArenaSelector.remove(playerUUID);
            return;
        }
        if (inv.getLocation() != null) {
            return;
        }
        if (!(inv == MemoryStorage.duelInventories.get(playerUUID))) {
            return;
        }

        if (PlayersWhoSentRequest.contains(playerUUID)) {
            PlayersWhoSentRequest.remove(playerUUID);
            return;
        }
        DuelRequest request = MemoryStorage.inventoryRequests.get(playerUUID);
        request.removeStoreRequest(false);
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.cancel.request-cancelled"));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Inventory inv = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        if (inv.getLocation() != null || !(inv == MemoryStorage.duelInventories.get(playerUUID)) || item == null) {
            return;
        }

        event.setCancelled(true);
        //always!!! get this request from the GUI clicker. because we are storing only sender: request in the requests map.
        DuelRequest request = MemoryStorage.inventoryRequests.get(playerUUID);
        Player target = Bukkit.getPlayer(request.getOpponent(playerUUID));
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            request.removeStoreRequest(false);
            player.closeInventory();
            return;
        }
        DuelRestrictions restrictions = request.getGame().getRestrictions();
        boolean isBowEnabled = restrictions.isBowAllowed();
        boolean isTotemEnabled = restrictions.isTotemAllowed();
        boolean isGPEnabled = restrictions.isGoldenAppleAllowed();
        boolean isNotchEnabled = restrictions.isNotchAllowed();
        boolean isPotionsEnabled = restrictions.isPotionAllowed();
        boolean isShieldsEnabled = restrictions.isShieldAllowed();
        boolean isElytraEnabled = restrictions.isElytraAllowed();
        boolean isEnderPearlEnabled = restrictions.isEnderPearlAllowed();
        boolean isKeepInventoryEnabled = restrictions.isKeepInventoryEnabled();
        boolean isInventorySavingEnabled = restrictions.isInventorySavingEnabled();
        int bowSlot = plugin.languageConfig.getInt("duel-GUI.toggle-bow.slot");
        int totemSlot = plugin.languageConfig.getInt("duel-GUI.toggle-totem.slot");
        int GPSlot = plugin.languageConfig.getInt("duel-GUI.toggle-golden-apple.slot");
        int NotchSlot = plugin.languageConfig.getInt("duel-GUI.toggle-enchanted-golden-apple.slot");
        int potionsSlot = plugin.languageConfig.getInt("duel-GUI.toggle-potion.slot");
        int shieldsSlot = plugin.languageConfig.getInt("duel-GUI.toggle-shield.slot");
        int elytraSlot = plugin.languageConfig.getInt("duel-GUI.toggle-elytra.slot");
        int enderpearlSlot = plugin.languageConfig.getInt("duel-GUI.toggle-ender-pearl.slot");
        int keepInventorySlot = plugin.languageConfig.getInt("duel-GUI.toggle-keep-inventory.slot");
        int inventorySavingSlot = plugin.languageConfig.getInt("duel-GUI.toggle-inventory-saving.slot");
        int arenaSelectorSlot = plugin.languageConfig.getInt("duel-GUI.arena-selector.slot");
        int startSlot = plugin.languageConfig.getInt("duel-GUI.start.slot");
        int cancelSlot = plugin.languageConfig.getInt("duel-GUI.cancel.slot");
        ItemMeta meta = item.getItemMeta();
        int slot = event.getSlot();
        //not using switch because it does not support using not very reliable int getter from the config
        if (slot == bowSlot) {
            isBowEnabled = !isBowEnabled;
            restrictions.setBow(isBowEnabled);
            if (isBowEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == totemSlot) {
            isTotemEnabled = !isTotemEnabled;
            restrictions.setTotem(isTotemEnabled);
            if (isTotemEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == GPSlot) {
            isGPEnabled = !isGPEnabled;
            restrictions.setGoldenApple(isGPEnabled);
            if (isGPEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == NotchSlot) {
            isNotchEnabled = !isNotchEnabled;
            restrictions.setNotch(isNotchEnabled);
            if (isNotchEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == potionsSlot) {
            isPotionsEnabled = !isPotionsEnabled;
            restrictions.setPotionAllowed(isPotionsEnabled);
            if (isPotionsEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-potion.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-potion.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == shieldsSlot) {
            isShieldsEnabled = !isShieldsEnabled;
            restrictions.setShield(isShieldsEnabled);
            if (isShieldsEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-shield.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-shield.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == elytraSlot) {
            isElytraEnabled = !isElytraEnabled;
            restrictions.setElytra(isElytraEnabled);
            if (isElytraEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-elytra.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-elytra.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == enderpearlSlot) {
            isEnderPearlEnabled = !isEnderPearlEnabled;
            restrictions.setEnderPearl(isEnderPearlEnabled);
            if (isEnderPearlEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-ender-pearl.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-ender-pearl.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (keepInventorySlot == slot) {
            isKeepInventoryEnabled = !isKeepInventoryEnabled;
            restrictions.setKeepInventory(isKeepInventoryEnabled);
            if (isKeepInventoryEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-keep-inventory.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-keep-inventory.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (inventorySavingSlot == slot) {
            isInventorySavingEnabled = !isInventorySavingEnabled;
            restrictions.setInventorySaving(isInventorySavingEnabled);
            if (isInventorySavingEnabled) {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-inventory-saving.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-inventory-saving.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == arenaSelectorSlot) {
            playersWhoClosedBecauseOfArenaSelector.add(playerUUID);
            request.getGame().setRestrictions(restrictions);
            Duels.guiManager.openArenaInventory(player, request);
        }else if (slot == startSlot) {
            restrictions.setComplete(true);
            //dont change position of player and target below
            request = RequestUtils.getRequestForCommands(target.getUniqueId(), playerUUID);
            request.getGame().setRestrictions(restrictions);

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
            double bet = request.getGame().getBet();
            if (plugin.getConfig().getBoolean("game.betting.enabled") && bet != 0) {
                Chat.sendMessage(target, plugin.languageConfig.getString("duel.betting.bet-amount").replace("%amount%", String.valueOf(bet)));
            }
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.duel.click").replace("%player%", player.getName()));
            PlayersWhoSentRequest.add(playerUUID);
            player.closeInventory();
        } else if (slot == cancelSlot) {
            player.closeInventory();
            request.removeStoreRequest(false);
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.cancel.request-cancelled"));
        }
    }
}
