package net.multylands.duels.commands.player.spectate;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.storage.MemoryStorage;
import net.multylands.duels.utils.SpectatorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopSpectateCommand implements CommandExecutor {
    public Duels plugin;

    public StopSpectateCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 0) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label)+" stopspectate");
            return false;
        }
        if (!MemoryStorage.spectators.containsKey(player.getUniqueId())) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.spectate.not-in-spectator"));
            return false;
        }
        SpectatorUtils.endSpectating(player, plugin);
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.spectate.spectate-end-success"));
        return false;
    }
}
