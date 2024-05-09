package net.multylands.duels.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class SavingItems {
    public static HashMap<UUID, HashMap<Integer, ItemStack>> savedInventories = new HashMap<>();

    public static void saveAndClearInventoryIfEnabled(Player player) {
        UUID playerUUID = player.getUniqueId();
        HashMap<Integer, ItemStack> inv = new HashMap<>();
        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            inv.put(slot, player.getInventory().getItem(slot));
        }
        savedInventories.put(playerUUID, inv);
        player.getInventory().clear();
    }

    public static void clearInvAndGiveItemsBack(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (savedInventories.get(playerUUID) == null) {
            return;
        }
        player.getInventory().clear();
        HashMap<Integer, ItemStack> inv = savedInventories.get(playerUUID);
        for (int slot : inv.keySet()) {
            player.getInventory().setItem(slot, inv.get(slot));
        }
        savedInventories.put(playerUUID, null);
    }
    public static void giveItemsBackIfAvailable(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (savedInventories.get(playerUUID) == null) {
            return;
        }
        HashMap<Integer, ItemStack> inv = savedInventories.get(playerUUID);
        for (int slot : inv.keySet()) {
            player.getInventory().setItem(slot, inv.get(slot));
        }
        savedInventories.put(playerUUID, null);
    }
}
