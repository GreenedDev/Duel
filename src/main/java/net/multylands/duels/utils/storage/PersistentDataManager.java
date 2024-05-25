package net.multylands.duels.utils.storage;

import net.multylands.duels.Duels;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataManager {
    public static String arenaKey = "arena_name";
    public static void setArenaName(Duels plugin, ItemStack item, String name) {
        NamespacedKey key = new NamespacedKey(plugin, arenaKey);
        ItemMeta oldMeta = item.getItemMeta();
        oldMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, name);
        item.setItemMeta(oldMeta);
    }
    public static String getArenaName(Duels plugin, ItemStack item) {
        NamespacedKey key = new NamespacedKey(plugin, arenaKey);
        return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
}
