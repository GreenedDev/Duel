package net.multylands.duels.object;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.RequestUtils;
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

    public void setSender(UUID player) {
        this.senderUUID = player;
    }

    public void setTarget(UUID target) {
        this.targetUUID = target;
    }

    public void storeRequest(boolean justStarted) {
        Set<DuelRequest> requestsWithoutThisRequestReceiverToSenders = RequestUtils.getRequestsReceiverToSenders(targetUUID, senderUUID);

        //second map
        Set<DuelRequest> requestsWithoutThisRequestSenderToReceiver = RequestUtils.getRequestsSenderToReceivers(senderUUID, targetUUID);

        if (justStarted) {
            Duels.playerToOpponentInGame.put(senderUUID, targetUUID);
            Duels.playerToOpponentInGame.put(targetUUID, senderUUID);
        }
        int taskIDOfTheTimeout = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (getGame().getIsInGame()) {
                return;
            }
            removeStoreRequest(false);
        }, plugin.getConfig().getInt("request-timeout") * 20).getTaskId();
        taskIdForRequestTimeout.add(taskIDOfTheTimeout);

        //do not move the below code up because the taskid will not be saved then
        requestsWithoutThisRequestReceiverToSenders.add(this);
        requestsWithoutThisRequestSenderToReceiver.add(this);
        Duels.requestsReceiverToSenders.put(targetUUID, requestsWithoutThisRequestReceiverToSenders);
        Duels.requestsSenderToReceivers.put(senderUUID, requestsWithoutThisRequestSenderToReceiver);
    }

    public void removeStoreRequest(boolean justEnded) {
        Iterator<Integer> iterator = taskIdForRequestTimeout.iterator();
        iterator.forEachRemaining(taskIDOfTheTimeout -> {
            Bukkit.getScheduler().cancelTask(taskIDOfTheTimeout);
        });
        taskIdForRequestTimeout.clear();
        Set<DuelRequest> requestsWithoutThisRequestReceiverToSenders = RequestUtils.getRequestsReceiverToSenders(targetUUID, senderUUID);

        Set<DuelRequest> requestsWithoutThisRequestSenderToReceiver = RequestUtils.getRequestsSenderToReceivers(senderUUID, targetUUID);
        if (requestsWithoutThisRequestSenderToReceiver.isEmpty()) {
            Duels.requestsSenderToReceivers.remove(senderUUID);
        } else {
            Duels.requestsSenderToReceivers.put(senderUUID, requestsWithoutThisRequestSenderToReceiver);
        }
        if (requestsWithoutThisRequestReceiverToSenders.isEmpty()) {
            Duels.requestsReceiverToSenders.remove(targetUUID);
        } else {
            Duels.requestsReceiverToSenders.put(targetUUID, requestsWithoutThisRequestReceiverToSenders);
        }
        if (justEnded) {
            Duels.playerToOpponentInGame.remove(senderUUID);
            Duels.playerToOpponentInGame.remove(targetUUID);
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
