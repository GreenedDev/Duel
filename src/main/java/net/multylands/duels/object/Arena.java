package net.multylands.duels.object;

import net.multylands.duels.Duels;
import org.bukkit.Location;

import java.util.UUID;
import java.util.logging.Level;

public class Arena {
    public boolean isAvailable = true;
    public UUID sender;
    public UUID target;
    public String ID;
    public Location loc1;
    public Location loc2;

    public Arena(Location loc1, Location loc2, UUID sender, UUID target, String ID) {
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.sender = sender;
        this.target = target;
        this.ID = ID;
    }

    public Boolean isAvailable() {
        return isAvailable;
    }

    public Location getFirstLocation(Duels plugin) {
        if (loc1 == null) {
            plugin.getLogger().log(Level.INFO, "Position1 is null (getFirstLocation();)");
        }
        return loc1;
    }

    public Location getSecondLocation(Duels plugin) {
        if (loc2 == null) {
            plugin.getLogger().log(Level.INFO, "Position2 is null (getSecondLocation();)");
        }
        return loc2;
    }

    public String getID() {
        return ID;
    }

    public UUID getSenderUUID() {
        return sender;
    }

    public UUID getTargetUUID() {
        return target;
    }

    public void setSenderUUID(UUID uuid) {
        this.target = uuid;
        Duels.Arenas.put(getID(), this);
    }

    public void setTargetUUID(UUID uuid) {
        this.target = uuid;
        Duels.Arenas.put(getID(), this);
    }

    public void setAvailable(boolean value) {
        this.isAvailable = value;
        Duels.Arenas.put(getID(), this);
    }
}
