package net.multylands.duels.gui;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.storage.MemoryStorage;
import net.multylands.duels.utils.storage.PersistentDataManager;
import net.multylands.duels.utils.storage.config.ItemUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ArenaInventoryHolder implements InventoryHolder {
    private static Inventory inventory;
    public static String arenaGUIPath = "arena-GUI";

    public ArenaInventoryHolder(Duels plugin, int size) {
        inventory = plugin.getServer().createInventory(this, size, Chat.color(plugin.languageConfig.getString(arenaGUIPath + ".title")));
        int slot = 0;
        for (Arena arena : MemoryStorage.Arenas.values()) {
            String arenaName = arena.getID();
            createItem(plugin, arenaName, arena.isAvailable(), slot);
            slot++;
        }
        String backItemName = "back";
        ItemStack backItem = ItemUtils.getItemFromConfigAndReplace(plugin.languageConfig, arenaGUIPath, backItemName, new HashMap<>(), new HashMap<>());
        inventory.setItem(plugin.languageConfig.getInt(arenaGUIPath + "." + backItemName + ".slot"), backItem);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static void createItem(Duels plugin, String arenaName, boolean availability, int slot) {
        String available = plugin.languageConfig.getString(arenaGUIPath + ".available");
        String unavailable = plugin.languageConfig.getString(arenaGUIPath + ".unavailable");

        String not_selected = plugin.languageConfig.getString(arenaGUIPath + ".not-selected");

        String nameReplacement;
        if (availability) {
            nameReplacement = available;
        } else {
            nameReplacement = unavailable;
        }
        HashMap<String, String> nameReplacements = new HashMap<>();
        nameReplacements.put("%name%", arenaName);
        nameReplacements.put("%available%", nameReplacement);

        HashMap<String, String> loreReplacements = new HashMap<>();
        loreReplacements.put("%status%", not_selected);

        ItemStack arenaItem = ItemUtils.getItemFromConfigAndReplace(plugin.languageConfig, arenaGUIPath, "format", nameReplacements, loreReplacements);
        PersistentDataManager.setArenaName(plugin, arenaItem, arenaName);
        inventory.setItem(slot, arenaItem);
    }
}