package net.multylands.duels.placeholders;

import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.multylands.duels.Duels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class MiniPlaceholders {
    public static void implementMiniPlaceholders(Duels plugin) {
        if (!Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) { //
            plugin.getLogger().log(Level.WARNING, "Could not find MiniPlaceholders! This is not required to install.");
            return;
        }
        Expansion.Builder builder = Expansion.builder("duel");
        builder.audiencePlaceholder("opponent", (audience, queue, ctx) -> {
            Player player = (Player) audience;
            return Tag.selfClosingInserting(Component.text(CalculatePlaceholders.getOpponent(player)));
        });
        builder.audiencePlaceholder("opponent_ping", (audience, queue, ctx) -> {
            Player player = (Player) audience;
            return Tag.selfClosingInserting(Component.text(CalculatePlaceholders.getOpponentPing(player)));
        });
        builder.audiencePlaceholder("time_left", (audience, queue, ctx) -> {
            Player player = (Player) audience;
            return Tag.selfClosingInserting(Component.text(CalculatePlaceholders.getTimeLeft(player, plugin)));
        });
        builder.audiencePlaceholder("spectators", (audience, queue, ctx) -> {
            Player player = (Player) audience;
            return Tag.selfClosingInserting(Component.text(CalculatePlaceholders.getNumberOfSpectators(player)));
        });
        builder.build().register();
    }

}
