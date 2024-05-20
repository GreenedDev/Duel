package net.multylands.duels.gui;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.utils.Chat;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {
    public Duels plugin;

    public GUIManager(Duels plugin) {
        this.plugin = plugin;
    }

    public List<String> lore = new ArrayList<>();

    public void openInventory(Player sender, Player target, double bet) {
        sender.closeInventory();
        DuelRestrictions restrictions = new DuelRestrictions(true, true, true, true, true, true, true, true, false, false, false);
        DuelRequest request = new DuelRequest(sender.getUniqueId(), target.getUniqueId(), restrictions, false, false, bet, plugin);
        DuelInventoryHolder inventoryHolder = new DuelInventoryHolder(plugin, plugin.duelInventorySize, request);
        Inventory inventory = inventoryHolder.getInventory();
        ItemStack start = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.start.item")));
        ItemMeta startMeta = start.getItemMeta();
        startMeta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.start.display-name")));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.start.lore")) {
            lore.add(Chat.color(loreLine.replace("%player%", target.getName())));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.start.glowing")) {
            start.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            startMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        startMeta.setLore(lore);
        start.setItemMeta(startMeta);
        lore.clear();
        int startSlot = plugin.languageConfig.getInt("duel-GUI.start.slot");
        inventory.setItem(startSlot, start);

        sender.openInventory(inventory);

        request.storeRequest(false);
    }
}
