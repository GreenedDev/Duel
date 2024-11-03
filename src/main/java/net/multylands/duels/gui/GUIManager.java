package net.multylands.duels.gui;

import net.kyori.adventure.text.Component;
import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIManager {
    public Duels plugin;

    public GUIManager(Duels plugin) {
        this.plugin = plugin;
    }

    public static List<Component> lore = new ArrayList<>();


    public void openDuelInventory(Player sender, Player target, double bet, DuelRestrictions restrictions) {
        UUID senderUUID = sender.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        Inventory invFromHashMap = MemoryStorage.duelInventories.get(senderUUID);
        DuelRequest request = new DuelRequest(senderUUID, targetUUID, restrictions, false, false, bet, plugin);
        if (invFromHashMap != null) {
            setStartItem(plugin, invFromHashMap, target.getName());
            sender.openInventory(invFromHashMap);
            request.storeRequest(false);
            MemoryStorage.inventoryRequests.put(senderUUID, request);
            return;
        }
        DuelInventoryHolder inventoryHolder = new DuelInventoryHolder(plugin, plugin.duelInventorySize, request);
        Inventory inventory = inventoryHolder.getInventory();
        setStartItem(plugin, inventory, target.getName());

        sender.openInventory(inventory);
        MemoryStorage.duelInventories.put(senderUUID, inventory);
        request.storeRequest(false);
        MemoryStorage.inventoryRequests.put(senderUUID, request);
    }

    public void openArenaInventory(Player sender) {
        UUID senderUUID = sender.getUniqueId();
        Inventory invFromHashMap = MemoryStorage.arenaInventories.get(senderUUID);
        DuelRequest reqFromHashMap = MemoryStorage.inventoryRequests.get(senderUUID);
        if (invFromHashMap != null && reqFromHashMap != null) {
            sender.openInventory(invFromHashMap);
            return;
        }
        ArenaInventoryHolder inventoryHolder = new ArenaInventoryHolder(plugin, plugin.arenaInventorySize);
        Inventory inventory = inventoryHolder.getInventory();
        MemoryStorage.arenaInventories.put(senderUUID, inventory);

        sender.openInventory(inventory);
    }

    public static void setStartItem(Duels plugin, Inventory inventory, String targetName) {
        ItemStack start = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.start.item")));
        ItemMeta startMeta = start.getItemMeta();
        startMeta.displayName(Chat.parseLegacyOrModern(plugin.languageConfig.getString("duel-GUI.start.display-name")));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.start.lore")) {
            lore.add(Chat.parseLegacyOrModern(loreLine.replace("%player%", targetName)));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.start.glowing")) {
            start.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            startMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        startMeta.lore(lore);
        start.setItemMeta(startMeta);
        lore.clear();
        int startSlot = plugin.languageConfig.getInt("duel-GUI.start.slot");
        inventory.setItem(startSlot, start);
    }
}
