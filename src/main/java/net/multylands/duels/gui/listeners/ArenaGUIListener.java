package net.multylands.duels.gui.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.storage.MemoryStorage;
import net.multylands.duels.utils.storage.PersistentDataManager;
import net.multylands.duels.utils.storage.config.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class ArenaGUIListener implements Listener {
    Duels plugin;

    public ArenaGUIListener(Duels plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onGuiClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getLocation() != null) {
            return;
        }
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!(inv == MemoryStorage.arenaInventories.get(playerUUID))) {
            return;
        }
        DuelRequest request = MemoryStorage.inventoryRequests.get(playerUUID);
        Player target = Bukkit.getPlayer(request.getOpponent(playerUUID));
        if (target == null) {
            request.removeStoreRequest(false);
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Duels.guiManager.openDuelInventory(player, target, request.getGame().getBet(), request.getGame().getRestrictions());
        }, 1L);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Inventory inv = event.getClickedInventory();
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        if (event.getInventory() == MemoryStorage.arenaInventories.get(playerUUID)) {
            event.setCancelled(true);
        }
        if (!(inv == MemoryStorage.arenaInventories.get(playerUUID)) || item == null) {
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

        if (slot == 53) {
            inv.close();
        } else {

            String not_selected = plugin.languageConfig.getString("arena-GUI.not-selected");
            String selected = plugin.languageConfig.getString("arena-GUI.selected");

            String arenaName = PersistentDataManager.getArenaName(plugin, item);
            Arena arena = MemoryStorage.Arenas.get(arenaName);
            ItemMeta meta = item.getItemMeta();

            if (!arena.isAvailable()) {
                Chat.sendMessage(player, plugin.languageConfig.getString("duel.arena-not-available"));
                return;
            }
            HashMap<String, String> replacements = new HashMap<>();
            if (MemoryStorage.selectedArenas.get(playerUUID) == null || !MemoryStorage.selectedArenas.get(playerUUID).getID().equals(arenaName)) {
                replacements.put("%status%", selected);
                MemoryStorage.selectedArenas.put(playerUUID, arena);
                updateSelected(plugin, inv, arenaName);
            } else {
                replacements.put("%status%", not_selected);
                MemoryStorage.selectedArenas.put(playerUUID, null);
            }
            meta.lore(ItemUtils.getLoreComponents(plugin.languageConfig.getStringList("arena-GUI.format.lore"), replacements));
            item.setItemMeta(meta);
        }
    }

    public static void updateSelected(Duels plugin, Inventory inventory, String arenaName) {
        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            String itemsArenaName = PersistentDataManager.getArenaName(plugin, item);
            if (itemsArenaName == null || itemsArenaName.equals(arenaName)) {
                continue;
            }
            String not_selected = Chat.color(plugin.languageConfig.getString("arena-GUI.not-selected"));
            ItemMeta itemMeta = item.getItemMeta();
            HashMap<String, String> replacements = new HashMap<>();
            replacements.put("%status%", not_selected);

            itemMeta.lore(ItemUtils.getLoreComponents(plugin.languageConfig.getStringList("arena-GUI.format.lore"), replacements));

            item.setItemMeta(itemMeta);
        }
    }
}
