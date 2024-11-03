package net.multylands.duels.gui.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import net.multylands.duels.utils.storage.MemoryStorage;
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
        Inventory inv = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        if (event.getInventory() == MemoryStorage.duelInventories.get(playerUUID)) {
            event.setCancelled(true);
        }
        if (!(inv == MemoryStorage.duelInventories.get(playerUUID)) || item == null) {
            return;
        }
        event.setCancelled(true);
        //always!!! get this request from the GUI clicker. because we are storing only sender: request in the requests map.
        DuelRequest request = MemoryStorage.inventoryRequests.get(playerUUID);
        UUID targetUUID = request.getOpponent(playerUUID);
        Player target = Bukkit.getPlayer(request.getOpponent(playerUUID));
        if (target == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            request.removeStoreRequest(false);
            player.closeInventory();
            return;
        }
        DuelRestrictions restrictions = request.getGame().getRestrictions();
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
            restrictions.setBow(!restrictions.isBowAllowed());
            if (restrictions.isBowAllowed()) {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == totemSlot) {
            restrictions.setTotem(!restrictions.isTotemAllowed());
            if (restrictions.isTotemAllowed()) {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == GPSlot) {
            restrictions.setGoldenApple(!restrictions.isGoldenAppleAllowed());
            if (restrictions.isGoldenAppleAllowed()) {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == NotchSlot) {
            restrictions.setNotch(!restrictions.isNotchAllowed());
            if (restrictions.isNotchAllowed()) {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == potionsSlot) {
            restrictions.setPotionAllowed(!restrictions.isPotionAllowed());
            if (restrictions.isPotionAllowed()) {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-potion.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-potion.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == shieldsSlot) {
            restrictions.setShield(!restrictions.isShieldAllowed());
            if (restrictions.isShieldAllowed()) {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-shield.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-shield.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == elytraSlot) {
            restrictions.setElytra(!restrictions.isElytraAllowed());
            if (restrictions.isElytraAllowed()) {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-elytra.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-elytra.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == enderpearlSlot) {
            restrictions.setEnderPearl(!restrictions.isEnderPearlAllowed());
            if (restrictions.isEnderPearlAllowed()) {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-ender-pearl.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-ender-pearl.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (keepInventorySlot == slot) {
            restrictions.setKeepInventory(!restrictions.isKeepInventoryEnabled());
            if (restrictions.isKeepInventoryEnabled()) {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-keep-inventory.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-keep-inventory.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (inventorySavingSlot == slot) {
            restrictions.setInventorySaving(!restrictions.isInventorySavingEnabled());
            if (restrictions.isInventorySavingEnabled()) {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-inventory-saving.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.toggle-inventory-saving.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        } else if (slot == arenaSelectorSlot) {
            playersWhoClosedBecauseOfArenaSelector.add(playerUUID);
            request.getGame().setRestrictions(restrictions);
            Duels.guiManager.openArenaInventory(player);
        } else if (slot == startSlot) {
            restrictions.setComplete(true);
            //don't change position of player and target below
            request = RequestUtils.getRequestForCommands(targetUUID, playerUUID);
            request.getGame().setRestrictions(restrictions);

            request.storeRequest(false);
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.duel.request-sent").replace("%player%", target.getName()));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.commands.duel.request-received").replace("%player%", player.getName()));
            Chat.sendMessage(target, plugin.languageConfig.getString("duel.restrictions-modules.restrictions"));
            if (restrictions.getEnabled() != null) {
                Chat.sendMessage(target, plugin.languageConfig.getString("duel.restrictions-modules.enabled-restrictions") + request.getGame().getRestrictions().getEnabled());
            }
            if (restrictions.getDisabled() != null) {
                Chat.sendMessage(target, plugin.languageConfig.getString("duel.restrictions-modules.disabled-restrictions") + request.getGame().getRestrictions().getDisabled());
            }
            String enabled = plugin.languageConfig.getString("duel.other-modules.enabled");
            String disabled = plugin.languageConfig.getString("duel.other-modules.disabled");
            if (restrictions.isKeepInventoryEnabled()) {
                Chat.sendMessage(target, plugin.languageConfig.getString("duel.other-modules.keep-inventory").replace("%toggled%", enabled));
            } else {
                Chat.sendMessage(target, plugin.languageConfig.getString("duel.other-modules.keep-inventory").replace("%toggled%", disabled));
            }
            if (restrictions.isInventorySavingEnabled()) {
                Chat.sendMessage(target, plugin.languageConfig.getString("duel.other-modules.inventory-saving").replace("%toggled%", enabled));
            } else {
                Chat.sendMessage(target, plugin.languageConfig.getString("duel.other-modules.inventory-saving").replace("%toggled%", disabled));
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
