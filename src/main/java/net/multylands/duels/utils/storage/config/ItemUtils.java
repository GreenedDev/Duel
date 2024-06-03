package net.multylands.duels.utils.storage.config;

import net.kyori.adventure.text.Component;
import net.multylands.duels.utils.Chat;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemUtils {
    public static List<Component> lore = new ArrayList<>();

    public static ItemStack getItemFromConfigAndReplace(ConfigurationSection config, String path, String name, HashMap<String, String> nameReplacements, HashMap<String, String> loreReplacements) {
        String itemPath = path + "." + name;
        Material material = Material.getMaterial(config.getString(itemPath + ".item"));
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        if (config.getBoolean(itemPath + ".glowing")) {
            itemMeta.addEnchant(Enchantment.LURE, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        String displayName = config.getString(itemPath + ".display-name");
        displayName = replacePlaceholders(displayName, nameReplacements);
        Component displayNameComponent = Chat.parseLegacyOrModern(displayName);

        itemMeta.displayName(displayNameComponent);

        itemMeta.lore(getLoreComponents(config.getStringList(itemPath + ".lore"), loreReplacements));
        item.setItemMeta(itemMeta);
        lore.clear();
        return item;
    }

    public static String replacePlaceholders(String text, HashMap<String, String> placeholders) {
        for (String key : placeholders.keySet()) {
            text = text.replace(key, placeholders.get(key));
        }
        return text;
    }

    public static List<Component> getLoreComponents(List<String> lore, HashMap<String, String> placeholders) {
        List<Component> result = new ArrayList<>();
        for (String loreLine : lore) {
            String line = replacePlaceholders(loreLine, placeholders);
            result.add(Chat.parseLegacyOrModern(line));
        }
        return result;
    }
}
