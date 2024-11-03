package net.multylands.duels.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class Chat {
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void sendMessage(Player player, String message) {
        if (message.isEmpty()) {
            return;
        }
        if (message.startsWith("$")) {
            Component parsed = ServerUtils.miniMessage().deserialize(message.substring(1));
            player.sendMessage(parsed);
        } else {
            player.sendMessage(color(message));
        }
    }

    public static void sendMessageSender(CommandSender sender, String message) {
        if (message.isEmpty()) {
            return;
        }
        if (message.startsWith("$")) {
            Component parsed = ServerUtils.miniMessage().deserialize(message.substring(1));
            sender.sendMessage(parsed);
        } else {
            sender.sendMessage(color(message));
        }
    }

    public static void messagePlayers(Player player, Player target, String message) {
        Chat.sendMessage(player, message);
        Chat.sendMessage(target, message);
    }

    public static String getColorForNumber(AtomicInteger countdown) {
        if (countdown.get() == 5) {
            return "&4";
        } else if (countdown.get() == 4) {
            return "&c";
        } else if (countdown.get() == 3) {
            return "&6";
        } else if (countdown.get() == 2) {
            return "&2";
        } else if (countdown.get() == 1) {
            return "&a";
        }
        return "";
    }

    public static Component parseLegacyOrModern(String text) {
        if (text.startsWith("$")) {
            return MiniMessage.miniMessage().deserialize(text.substring(1));
        } else {
            if (text.contains("§")) {
                return LegacyComponentSerializer.legacySection().deserialize(text).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            } else {
                return LegacyComponentSerializer.legacyAmpersand().deserialize(text).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            }
        }
    }
}
