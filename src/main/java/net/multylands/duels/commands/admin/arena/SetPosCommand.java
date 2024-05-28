package net.multylands.duels.commands.admin.arena;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPosCommand implements CommandExecutor {
    public Duels plugin;

    public SetPosCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("duels.admin.setpos")) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("no-perm"));
            return false;
        }
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 2) {
            Chat.sendMessage(player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " setarenapos arenaName pos1/2");
            return false;
        }
        String arenaName = args[0];
        String pos = args[1].toLowerCase();
        if (!plugin.arenasConfig.contains(arenaName)) {
            Chat.sendMessage(player, plugin.languageConfig.getString("admin.set-pos.wrong-arena"));
            return false;
        }
        if (!pos.equals("pos1") && !pos.equals("pos2")) {
            Chat.sendMessage(player, plugin.languageConfig.getString("admin.set-pos.wrong-pos"));
            return false;
        }
        plugin.arenasConfig.set(arenaName + "." + pos, player.getLocation());
        //just removing the temporary value below
        plugin.arenasConfig.set(arenaName + ".isnew", null);
        plugin.saveArenasConfig();
        Chat.sendMessage(player, plugin.languageConfig.getString("admin.set-pos.success").replace("%pos%", pos));

        if (plugin.arenasConfig.getLocation(arenaName + ".pos1") == null
                || plugin.arenasConfig.getLocation(arenaName + ".pos2") == null) {
            return false;
        }
        Location loc1 = plugin.arenasConfig.getLocation(arenaName + ".pos1");
        Location loc2 = plugin.arenasConfig.getLocation(arenaName + ".pos2");
        Arena arena = new Arena(loc1, loc2, null, null, arenaName);
        if (MemoryStorage.Arenas.containsKey(arenaName)) {
            //to prevent players getting lost when their arena was replaced.
            for (DuelRequest request : MemoryStorage.inGameDuels) {
                if (!request.getGame().getArena().getID().equals(arenaName)) {
                    continue;
                }
                request.getGame().endGameRestart();
            }
        }
        MemoryStorage.Arenas.put(arenaName, arena);
        Chat.sendMessage(player, plugin.languageConfig.getString("admin.set-pos.arena-loaded"));
        return false;
    }
}
