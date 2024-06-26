package net.multylands.duels.object;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.RequestUtils;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class DuelRequest {
    UUID senderUUID;
    UUID targetUUID;
    Duels plugin;
    Game game;
    ArrayList<Integer> taskIdForRequestTimeout = new ArrayList<>();

    public DuelRequest(UUID sender, UUID target, DuelRestrictions duelRestrictions, boolean isInGame, boolean isStartingIn5Seconds, double bet, Duels plugin) {
        this.senderUUID = sender;
        this.targetUUID = target;
        this.plugin = plugin;
        game = new Game(sender, target, this, duelRestrictions, isInGame, isStartingIn5Seconds, bet, plugin);
    }

    public Game getGame() {
        return game;
    }

    public UUID getSender() {
        return senderUUID;
    }

    public UUID getTarget() {
        return targetUUID;
    }


    public void storeRequest(boolean justStarted) {
        Set<DuelRequest> requestsWithoutThisRequestSenderToReceiver = RequestUtils.getRequestsSenderToReceivers(senderUUID, targetUUID);

        if (justStarted) {
            MemoryStorage.inGameDuels.add(this);
        }
        int taskIDOfTheTimeout = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (getGame().getIsInGame()) {
                return;
            }
            removeStoreRequest(false);
        }, plugin.getConfig().getInt("request-timeout") * 20L).getTaskId();
        taskIdForRequestTimeout.add(taskIDOfTheTimeout);

        //do not move the below code up because the taskID will not be saved then
        requestsWithoutThisRequestSenderToReceiver.add(this);
        MemoryStorage.requestsSenderToReceivers.put(senderUUID, requestsWithoutThisRequestSenderToReceiver);
    }

    public void removeStoreRequest(boolean justEnded) {
        Iterator<Integer> iterator = taskIdForRequestTimeout.iterator();
        iterator.forEachRemaining(taskIDOfTheTimeout -> {
            Bukkit.getScheduler().cancelTask(taskIDOfTheTimeout);
        });
        taskIdForRequestTimeout.clear();
        Set<DuelRequest> requestsWithoutThisRequestSenderToReceiver = RequestUtils.getRequestsSenderToReceivers(senderUUID, targetUUID);
        if (requestsWithoutThisRequestSenderToReceiver.isEmpty()) {
            MemoryStorage.requestsSenderToReceivers.remove(senderUUID);
        } else {
            MemoryStorage.requestsSenderToReceivers.put(senderUUID, requestsWithoutThisRequestSenderToReceiver);
        }
        if (justEnded) {
            MemoryStorage.inGameDuels.remove(this);
        }
    }

    public UUID getOpponent(UUID someone) {
        if (someone == senderUUID) {
            return targetUUID;
        } else if (someone == targetUUID) {
            return senderUUID;
        } else {
            plugin.getLogger().log(Level.INFO, "Plugin tried to get opponent of the player that's " +
                    "not in that duel object. please report this to the author immediately");
            return null;
        }
    }
}
