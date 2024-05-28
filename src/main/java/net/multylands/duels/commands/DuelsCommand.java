package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DuelsCommand implements CommandExecutor, TabCompleter {
    public Duels plugin;

    public DuelsCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || MemoryStorage.playerCommandExecutors.get(args[0]) == null) {
            for (String message : plugin.languageConfig.getStringList("duel.commands.help")) {
                sender.sendMessage(Chat.color(message));
            }
            return false;
        }
        CommandExecutor executor = MemoryStorage.playerCommandExecutors.get(args[0]);
        executor.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabCompleteStrings = new ArrayList<>();
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("queue") || args[0].equalsIgnoreCase("stopspectate")) {
                return tabCompleteStrings;
            }
            Bukkit.getOnlinePlayers().forEach(player -> {
                String playerName = player.getName().toLowerCase();
                String args1 = args[1].toLowerCase();
                if (playerName.startsWith(args1) && !player.getName().equals(sender.getName())) {
                    tabCompleteStrings.add(player.getName());
                }
            });
        } else {
            for (String commands : MemoryStorage.playerCommandExecutors.keySet()) {
                if (commands.startsWith(args[0])) {
                    if (!commands.equalsIgnoreCase(args[0])) {
                        tabCompleteStrings.add(commands);
                    }
                }
            }
        }
        return tabCompleteStrings;
    }
}
