package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class RequestUtils {
    public static DuelRequest getRequestOfTheDuelPlayerIsIn(UUID playerUUID) {
        UUID targetUUID = MemoryStorage.playerToOpponentInGame.get(playerUUID);
        if (targetUUID == null) {
            return null;
        }
        if (MemoryStorage.requestsReceiverToSenders.get(playerUUID) != null) {
            for (DuelRequest request : MemoryStorage.requestsReceiverToSenders.get(playerUUID)) {
                if (!((request.getSender() == playerUUID || request.getTarget() == playerUUID) && ((request.getSender() == targetUUID || request.getTarget() == targetUUID)))) {
                    continue;
                }
                return request;
            }
        }
        if (MemoryStorage.requestsSenderToReceivers.get(playerUUID) != null) {
            for (DuelRequest request : MemoryStorage.requestsSenderToReceivers.get(playerUUID)) {
                if (!((request.getSender() == playerUUID || request.getTarget() == playerUUID) && ((request.getSender() == targetUUID || request.getTarget() == targetUUID)))) {
                    continue;
                }
                return request;
            }
        }
        return null;
    }

    public static boolean isInGame(DuelRequest request) {
        if (request == null) {
            return false;
        }
        return request.getGame().getIsInGame();
    }

    public static DuelRequest getRequestForCommands(UUID receiverUUID, UUID senderUUID) {
        if (MemoryStorage.requestsReceiverToSenders.get(receiverUUID) == null) {
            return null;
        }
        for (DuelRequest request : MemoryStorage.requestsReceiverToSenders.get(receiverUUID)) {
            if (!(request.getSender() == senderUUID && request.getTarget() == receiverUUID)) {
                continue;
            }
            return request;
        }
        return null;
    }

    public static Set<DuelRequest> getRequestsReceiverToSenders(UUID targetUUID, UUID senderUUID) {
        Set<DuelRequest> requestsThatWereAlreadyThere = MemoryStorage.requestsReceiverToSenders.get(targetUUID);
        //checking if there was no value set for that key preventing requestsThatWereAlreadyThere to be null
        if (MemoryStorage.requestsReceiverToSenders.get(targetUUID) == null) {
            requestsThatWereAlreadyThere = new HashSet<>();
        } else {
            //removing the old request that was in map. so that when you add a new one duplicate doesnt happen
            Iterator<DuelRequest> iterator = requestsThatWereAlreadyThere.iterator();
            while (iterator.hasNext()) {
                DuelRequest request = iterator.next();
                if (request.getSender() == senderUUID && request.getTarget() == targetUUID) {
                    iterator.remove();
                    break;
                }
            }
        }
        return requestsThatWereAlreadyThere;
    }

    public static Set<DuelRequest> getRequestsSenderToReceivers(UUID senderUUID, UUID targetUUID) {
        Set<DuelRequest> requestsThatWereAlreadyThereSenderToReceiver = MemoryStorage.requestsSenderToReceivers.get(senderUUID);
        if (MemoryStorage.requestsSenderToReceivers.get(senderUUID) == null) {
            requestsThatWereAlreadyThereSenderToReceiver = new HashSet<>();
        } else {
            //removing the old request that was in map. so that when you add a new one duplicate doesnt happen
            Iterator<DuelRequest> iterator = requestsThatWereAlreadyThereSenderToReceiver.iterator();
            while (iterator.hasNext()) {
                DuelRequest request = iterator.next();
                if (request.getSender() == senderUUID && request.getTarget() == targetUUID) {
                    iterator.remove();
                    break;
                }
            }
        }
        return requestsThatWereAlreadyThereSenderToReceiver;
    }
}
