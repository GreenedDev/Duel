package net.multylands.duels.gui;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.storage.MemoryStorage;
import net.multylands.duels.utils.storage.PersistentDataManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ArenaInventoryHolder implements InventoryHolder {
    private static Inventory inventory;
    public static List<String> lore = new ArrayList<>();

    public ArenaInventoryHolder(Duels plugin, int size) {
        inventory = plugin.getServer().createInventory(this, size, Chat.color(plugin.languageConfig.getString("arena-GUI.title")));
        int slot = 0;
        for (Arena arena : MemoryStorage.Arenas.values()) {
            String arenaName = arena.getID();
            createItem(plugin, arenaName, arena.isAvailable(), slot);
            slot++;
        }
        ItemStack backItem = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("arena-GUI.back.item")));
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(Chat.color(plugin.languageConfig.getString("arena-GUI.back.display-name")));
        for (String loreLine : plugin.languageConfig.getStringList("arena-GUI.back.lore")) {
            lore.add(Chat.color(loreLine));
        }
        if (plugin.languageConfig.getBoolean("arena-GUI.cancel.glowing")) {
            backMeta.addEnchant(Enchantment.LURE, 1, true);
            backMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        backMeta.setLore(lore);
        backItem.setItemMeta(backMeta);
        inventory.setItem(plugin.languageConfig.getInt("arena-GUI.back.slot"), backItem);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static void createItem(Duels plugin, String arenaName, boolean availability, int slot) {
        String available = Chat.color(plugin.languageConfig.getString("arena-GUI.available"));
        String unavailable = Chat.color(plugin.languageConfig.getString("arena-GUI.unavailable"));

        String not_selected = Chat.color(plugin.languageConfig.getString("arena-GUI.not-selected"));

        String displayName = Chat.color(plugin.languageConfig.getString("arena-GUI.format.display-name"));
        if (availability) {
            displayName = displayName.replace("%available%", available);
        } else {
            displayName = displayName.replace("%available%", unavailable);
        }
        displayName = displayName.replace("%name%", arenaName);
        ItemStack arenaItem = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("arena-GUI.format.item")));
        ItemMeta arenaItemMeta = arenaItem.getItemMeta();
        arenaItemMeta.setDisplayName(displayName);
        for (String loreLine : plugin.languageConfig.getStringList("arena-GUI.format.lore")) {
            lore.add(Chat.color(loreLine.replace("%status%", not_selected)));
        }
        if (plugin.languageConfig.getBoolean("arena-GUI.cancel.glowing")) {
            arenaItemMeta.addEnchant(Enchantment.LURE, 1, true);
            arenaItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        arenaItemMeta.setLore(lore);
        arenaItem.setItemMeta(arenaItemMeta);
        PersistentDataManager.setArenaName(plugin, arenaItem, arenaName);
        inventory.setItem(slot, arenaItem);
        lore.clear();
    }
}