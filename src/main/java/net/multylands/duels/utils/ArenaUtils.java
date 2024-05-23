package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;

public class ArenaUtils {
    public static Arena getAvailableArena() {
        for (Arena arena : MemoryStorage.Arenas.values()) {
            if (!arena.isAvailable()) {
                continue;
            }
            return arena;
        }
        return null;
    }
}
