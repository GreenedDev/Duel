package net.multylands.duels.gui;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.Module;
import net.multylands.duels.utils.Chat;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DuelInventoryHolder implements InventoryHolder {
    int cancelSlot;
    private Inventory inventory;
    public List<String> lore = new ArrayList<>();
    public DuelRequest request;
    Duels plugin;

    public DuelInventoryHolder(Duels plugin, int size, DuelRequest request) {
        this.plugin = plugin;
        cancelSlot = plugin.languageConfig.getInt("duel-GUI.cancel.slot");
        this.request = request;
        this.inventory = plugin.getServer().createInventory(this, size, Chat.color(plugin.languageConfig.getString("duel-GUI.title")));
        addItemIfEnabled("toggle-bow", inventory, request.getGame().getRestrictions().isBowAllowed(), Module.RESTRICTION);
        addItemIfEnabled("toggle-totem", inventory, request.getGame().getRestrictions().isTotemAllowed(), Module.RESTRICTION);
        addItemIfEnabled("toggle-golden-apple", inventory, request.getGame().getRestrictions().isGoldenAppleAllowed(), Module.RESTRICTION);
        addItemIfEnabled("toggle-enchanted-golden-apple", inventory, request.getGame().getRestrictions().isNotchAllowed(), Module.RESTRICTION);
        addItemIfEnabled("toggle-potion", inventory, request.getGame().getRestrictions().isPotionAllowed(), Module.RESTRICTION);
        addItemIfEnabled("toggle-shield", inventory, request.getGame().getRestrictions().isShieldAllowed(), Module.RESTRICTION);
        addItemIfEnabled("toggle-elytra", inventory, request.getGame().getRestrictions().isElytraAllowed(), Module.RESTRICTION);
        addItemIfEnabled("toggle-ender-pearl", inventory, request.getGame().getRestrictions().isEnderPearlAllowed(), Module.RESTRICTION);
        addItemIfEnabled("toggle-keep-inventory", inventory, request.getGame().getRestrictions().isKeepInventoryEnabled(), Module.OTHER);
        addItemIfEnabled("toggle-inventory-saving", inventory, request.getGame().getRestrictions().isInventorySavingEnabled(), Module.OTHER);
        addItemIfEnabled("arena-selector", inventory, true, Module.ARENA_SELECTOR);
        ItemStack cancel = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.cancel.item")));
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.cancel.display-name")));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.cancel.lore")) {
            lore.add(Chat.color(loreLine));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.cancel.glowing")) {
            cancelMeta.addEnchant(Enchantment.LURE, 1, true);
            cancelMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        cancelMeta.setLore(lore);
        cancel.setItemMeta(cancelMeta);
        lore.clear();
        inventory.setItem(cancelSlot, cancel);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void addItemIfEnabled(String name, Inventory inventory, boolean toggled, Module module) {
        String nameWithoutToggle = name.replace("toggle-", "");
        if (module == Module.RESTRICTION) {
            if (!plugin.getConfig().getBoolean("modules.restrictions." + nameWithoutToggle+".enabled")) {
                return;
            }
        } else if (module == Module.ARENA_SELECTOR) {
            if (!(plugin.getConfig().getBoolean("modules." + nameWithoutToggle))) {
                return;
            }
        } else if (module == Module.OTHER){
            if (!(plugin.getConfig().getBoolean("modules." + nameWithoutToggle+".enabled"))) {
                return;
            }
        }
        ItemStack item = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI." + name + ".item")));
        ItemMeta itemMeta = item.getItemMeta();
        if (plugin.languageConfig.getBoolean("duel-GUI." + name + ".glowing")) {
            itemMeta.addEnchant(Enchantment.LURE, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (toggled) {
            itemMeta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI." + name + ".display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        } else {
            itemMeta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI." + name + ".display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
        }
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI." + name + ".lore")) {
            lore.add(Chat.color(loreLine));
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        lore.clear();

        int itemSlot = plugin.languageConfig.getInt("duel-GUI." + name + ".slot");
        inventory.setItem(itemSlot, item);
    }

}