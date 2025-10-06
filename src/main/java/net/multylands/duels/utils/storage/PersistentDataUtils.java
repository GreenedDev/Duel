package net.multylands.duels.utils.storage;

import net.multylands.duels.Duels;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;


public class PersistentDataUtils {
    public static final String IS_DUELS = "should_be_teleported_to_spawn";
    public static final String yes = "yes";
    public static final String no = "no";
    public static final DuelElementDataType hatElementDataType = new DuelElementDataType();

    public static void markPlayerAsQuitter(Duels plugin, Player player) {
        NamespacedKey isAdvancedHatsKey = new NamespacedKey(plugin, IS_DUELS);
        player.getPersistentDataContainer().set(isAdvancedHatsKey, PersistentDataType.STRING, yes);
    }

    public static void unmarkPlayerAsQuitter(Duels plugin, Player player) {
        NamespacedKey isAdvancedHatsKey = new NamespacedKey(plugin, IS_DUELS);
        player.getPersistentDataContainer().set(isAdvancedHatsKey, PersistentDataType.STRING, no);
    }

    public static boolean isPlayerQuitter(Duels plugin, Player display) {
        NamespacedKey key = new NamespacedKey(plugin, IS_DUELS);
        return Objects.equals(display.getPersistentDataContainer().get(key, PersistentDataType.STRING), yes);
    }
}