package net.multylands.duels.commands.player.spectate;

import net.multylands.duels.Duels;
import net.multylands.duels.commands.player.CheckPermissions;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import net.multylands.duels.utils.SpectatorUtils;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpectateCommand implements CommandExecutor {
    public Duels plugin;

    public SpectateCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (!CheckPermissions.hasPermission(plugin.getConfig(), "spectate", "duel.commands.spectate", player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("no-perm"));
            return true;
        }
        UUID playerUUID = player.getUniqueId();
        if (args.length != 1) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " spectate player");
            return false;
        }
        String toSpectateName = args[0];
        Player toSpectate = Bukkit.getPlayer(toSpectateName);
        if (toSpectate == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.target-is-offline"));
            return false;
        }
        UUID toSpectateUUID = toSpectate.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(toSpectateUUID);
        if (request == null) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.spectate.is-not-in-duel").replace("%player%", toSpectateName));
            return false;
        }
        if (MemoryStorage.spectators.containsKey(playerUUID)) {
            Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.spectate.already-spectating"));
            return false;
        }
        Player toSpectateOpponent = Bukkit.getPlayer(request.getOpponent(toSpectateUUID));
        SpectatorUtils.startSpectating(player, toSpectate, plugin);
        Chat.sendMessage(toSpectate, plugin.languageConfig.getString("duel.commands.spectate.is-spectating").replace("%player%", player.getName()));
        Chat.sendMessage(toSpectateOpponent, plugin.languageConfig.getString("duel.commands.spectate.is-spectating").replace("%player%", player.getName()));
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.commands.spectate.success").replace("%player%", toSpectateName));
        return false;
    }
}
