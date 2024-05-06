package net.multylands.duels.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {

    private final Duels plugin;

    public PlaceholderAPI(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "GreenedDev";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "duel";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("opponent")) {
            return CalculatePlaceholders.getOpponent(player);
        }
        if (params.equalsIgnoreCase("opponent_ping")) {
            return CalculatePlaceholders.getOpponentPing(player);
        }
        if (params.equalsIgnoreCase("time_left")) {
            return CalculatePlaceholders.getTimeLeft(player, plugin);
        }
        if (params.equalsIgnoreCase("spectators")) {
            return CalculatePlaceholders.getNumberOfSpectators(player);
        }
        return Chat.color(plugin.getConfig().getString("glowing-wrong-placeholder"));
    }
}