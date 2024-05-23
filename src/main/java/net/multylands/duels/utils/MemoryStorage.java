package net.multylands.duels.utils;

import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MemoryStorage {
    public static HashMap<String, Arena> Arenas = new HashMap<>();
    //storing only sender: request(withTargetName)
    public static HashMap<UUID, Set<DuelRequest>> requestsReceiverToSenders = new HashMap<>();
    public static HashMap<UUID, Set<DuelRequest>> requestsSenderToReceivers = new HashMap<>();
    //storing sender: player
    //and player: sender
    public static HashMap<UUID, UUID> playerToOpponentInGame = new HashMap<>();
    //storing uuid: taskID
    public static HashMap<UUID, Integer> tasksToCancel = new HashMap<>();
    //storing spectator: toSpectate
    public static HashMap<UUID, UUID> spectators = new HashMap<>();

    public static HashMap<UUID, Inventory> duelInventories = new HashMap<>();
    public static HashMap<UUID, Inventory> arenaInventories = new HashMap<>();

    public static HashMap<UUID, Arena> selectedArenas = new HashMap<>();
    public static HashMap<UUID, DuelRequest> inventoryRequests = new HashMap<>();

    public static HashMap<String, CommandExecutor> commandExecutors = new HashMap<>();

    public static HashMap<UUID, HashMap<Integer, ItemStack>> savedInventories = new HashMap<>();

    public static HashSet<UUID> listOfPlayersWhoShouldBeTeleportedToSpawnAfterRespawn = new HashSet<>();
}
