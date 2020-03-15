package com.github.kotooriiii.guards;

import org.bukkit.Location;

public class ShardLocationNPC extends ShardBaseNPC {
    private Location spawnLocation;

    /**
     * Creates a new NPC with the given name and following skin.
     *
     * @param prefix
     * @param name   The name of the NPC
     * @param skin   The skin of the NPC
     */
    public ShardLocationNPC(String prefix, String name, Skin skin) {
        super(prefix, name, skin);
    }

    public boolean setSpawnLocation(Location location) {
        if (isDestroyed() || !isSpawned())
            return false;
        Location guardLocation = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5);
        this.spawnLocation = guardLocation;
        teleport(location);
        return true;
    }

    protected boolean updateLocation(Location location)
    {
        this.spawnLocation = location;
        return true;

    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }
}
