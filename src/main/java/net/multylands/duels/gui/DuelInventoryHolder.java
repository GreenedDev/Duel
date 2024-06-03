package net.multylands.duels.gui;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.Module;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.storage.config.ItemUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DuelInventoryHolder implements InventoryHolder {
    int cancelSlot;
    private Inventory inventory;
    public static String duelGUIPath = "duel-GUI";
    Duels plugin;

    public DuelInventoryHolder(Duels plugin, int size, DuelRequest request) {
        this.plugin = plugin;
        cancelSlot = plugin.languageConfig.getInt(duelGUIPath + ".cancel.slot");
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
        ItemStack cancel = ItemUtils.getItemFromConfigAndReplace(plugin.languageConfig, duelGUIPath, "cancel", new HashMap<>(), new HashMap<>());
        inventory.setItem(cancelSlot, cancel);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void addItemIfEnabled(String name, Inventory inventory, boolean toggled, Module module) {
        String nameWithoutToggle = name.replace("toggle-", "");
        if (module == Module.RESTRICTION) {
            if (!plugin.getConfig().getBoolean("modules.restrictions." + nameWithoutToggle + ".enabled")) {
                return;
            }
        } else if (module == Module.ARENA_SELECTOR) {
            if (!(plugin.getConfig().getBoolean("modules." + nameWithoutToggle))) {
                return;
            }
        } else if (module == Module.OTHER) {
            if (!(plugin.getConfig().getBoolean("modules." + nameWithoutToggle + ".enabled"))) {
                return;
            }
        }
        String itemPath = "duel-GUI." + name;
        HashMap<String, String> nameReplacements = new HashMap<>();
        nameReplacements.put("%toggled%", getToggleReplacement(toggled, plugin.languageConfig));
        ItemStack item = ItemUtils.getItemFromConfigAndReplace(plugin.languageConfig, duelGUIPath, name, nameReplacements, new HashMap<>());

        int itemSlot = plugin.languageConfig.getInt(itemPath + ".slot");
        inventory.setItem(itemSlot, item);
    }

    public static String getToggleReplacement(boolean toggled, ConfigurationSection config) {
        String replacement;
        if (toggled) {
            replacement = config.getString(duelGUIPath + ".restriction-enabled");
        } else {
            replacement = config.getString(duelGUIPath + ".restriction-disabled");
        }
        return replacement;
    }
}