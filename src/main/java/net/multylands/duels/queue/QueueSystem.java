package net.multylands.duels.queue;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.utils.ArenaUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class QueueSystem {
    public static Set<UUID> playersInQueue = new HashSet<>();

    public static void checkQueue(Duels plugin) {
        System.out.println(1);
        if (playersInQueue.isEmpty()) {
            return;
        }
        System.out.println(2);
        if (playersInQueue.size() < 2) {
            return;
        }
        System.out.println(3);
        Arena availableArena = ArenaUtils.getAvailableArena();
        if (availableArena == null) {
            return;
        }
        System.out.println(4);
        List<UUID> twoPlayers = new ArrayList<>();
        for (UUID queuePlayerUUID : playersInQueue) {
            System.out.println(5);
            twoPlayers.add(queuePlayerUUID);
            if (twoPlayers.size() == 2) {
                Player player1 = Bukkit.getPlayer(twoPlayers.getFirst());
                Player player2 = Bukkit.getPlayer(twoPlayers.getLast());
                System.out.println(6);
                //make them go in duel
                DuelRestrictions restrictions = new DuelRestrictions(true, true, true, true, true, true, true, true, true, false, false);
                DuelRequest request = new DuelRequest(player1.getUniqueId(), player2.getUniqueId(), restrictions, false, false, 0, plugin);
                request.storeRequest(false);

                request.getGame().start(availableArena);

                twoPlayers.clear();
                continue;
            }
            System.out.println(twoPlayers);
        }
        twoPlayers.clear();
    }
}
