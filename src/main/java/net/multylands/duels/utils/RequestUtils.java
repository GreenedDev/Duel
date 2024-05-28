package net.multylands.duels.utils;

import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.storage.MemoryStorage;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class RequestUtils {
    public static DuelRequest getRequestOfTheDuelPlayerIsIn(UUID playerUUID) {
        for (DuelRequest request : MemoryStorage.inGameDuels) {
            if (request.getSender() == playerUUID || request.getTarget() == playerUUID) {
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
        if (MemoryStorage.requestsSenderToReceivers.get(senderUUID) == null) {
            return null;
        }
        for (DuelRequest request : MemoryStorage.requestsSenderToReceivers.get(senderUUID)) {
            if (!(request.getSender() == senderUUID && request.getTarget() == receiverUUID)) {
                continue;
            }
            return request;
        }
        return null;
    }


    public static Set<DuelRequest> getRequestsSenderToReceivers(UUID senderUUID, UUID targetUUID) {
        Set<DuelRequest> requestsThatWereAlreadyThereSenderToReceiver = MemoryStorage.requestsSenderToReceivers.get(senderUUID);
        if (requestsThatWereAlreadyThereSenderToReceiver == null) {
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

    public static Set<DuelRequest> getPlayerRequestsS_R(Player sender) {
        if (MemoryStorage.requestsSenderToReceivers.containsKey(sender.getUniqueId())) {
            return MemoryStorage.requestsSenderToReceivers.get(sender.getUniqueId());
        }
        return new HashSet<>();
    }
}
