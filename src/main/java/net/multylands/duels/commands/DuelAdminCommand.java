package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DuelAdminCommand implements CommandExecutor, TabCompleter {
    public Duels plugin;

    public DuelAdminCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || Duels.commandExecutors.get(args[0]) == null) {
            for (String message : plugin.languageConfig.getStringList("admin.help")) {
                sender.sendMessage(Chat.color(message));
            }
            return false;
        }
        CommandExecutor executor = Duels.commandExecutors.get(args[0]);
        executor.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabCompleteStrings = new ArrayList<>();
        for (String commands : Duels.commandExecutors.keySet()) {
            if (commands.startsWith(args[0])) {
                if (!commands.equalsIgnoreCase(args[0])) {
                    tabCompleteStrings.add(commands);
                }
            }
        }
        return tabCompleteStrings;
    }
}
