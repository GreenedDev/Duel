package net.multylands.duels.gui;

import net.multylands.duels.Duels;
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
        addItemIfEnabled("bow", inventory, request.getGame().getRestrictions().isBowAllowed(), Module.RESTRICTION);
        addItemIfEnabled("totem", inventory, request.getGame().getRestrictions().isTotemAllowed(), Module.RESTRICTION);
        addItemIfEnabled("golden-apple", inventory, request.getGame().getRestrictions().isGoldenAppleAllowed(), Module.RESTRICTION);
        addItemIfEnabled("enchanted-golden-apple", inventory, request.getGame().getRestrictions().isNotchAllowed(), Module.RESTRICTION);
        addItemIfEnabled("potion", inventory, request.getGame().getRestrictions().isPotionAllowed(), Module.RESTRICTION);
        addItemIfEnabled("shield", inventory, request.getGame().getRestrictions().isShieldAllowed(), Module.RESTRICTION);
        addItemIfEnabled("elytra", inventory, request.getGame().getRestrictions().isElytraAllowed(), Module.RESTRICTION);
        addItemIfEnabled("ender-pearl", inventory, request.getGame().getRestrictions().isEnderPearlAllowed(), Module.RESTRICTION);
        addItemIfEnabled("keep-inventory", inventory, request.getGame().getRestrictions().isKeepInventoryEnabled(), Module.OTHER);
        addItemIfEnabled("inventory-saving", inventory, request.getGame().getRestrictions().isInventorySavingEnabled(), Module.OTHER);
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

    public void setRequest(DuelRequest request) {
        this.request = request;
    }

    public DuelRequest getRequest() {
        return this.request;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void addItemIfEnabled(String name, Inventory inventory, boolean toggled, Module module) {
        if (module == Module.RESTRICTION) {
            if (!plugin.getConfig().getBoolean("modules.restrictions." + name)) {
                return;
            }
        } else {
            if (!(plugin.getConfig().getBoolean("modules." + name))) {
                return;
            }
        }
        ItemStack item = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-" + name + ".item")));
        ItemMeta itemMeta = item.getItemMeta();
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-" + name + ".glowing")) {
            itemMeta.addEnchant(Enchantment.LURE, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (toggled) {
            itemMeta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-" + name + ".display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        } else {
            itemMeta.setDisplayName(Chat.color(plugin.languageConfig.getString("duel-GUI.toggle-" + name + ".display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
        }
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-" + name + ".lore")) {
            lore.add(Chat.color(loreLine));
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        lore.clear();

        int itemSlot = plugin.languageConfig.getInt("duel-GUI.toggle-" + name + ".slot");
        inventory.setItem(itemSlot, item);
    }

}