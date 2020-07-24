package com.github.kotooriiii.status.shrine;


import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Location;

import java.util.UUID;

public abstract class Shrine {
    private Location location;
    private ShrineType type;
    private UUID uuid;

    public Shrine(ShrineType type) {
        this.type = type;
        this.uuid = generateUUID();
    }
    public Shrine(ShrineType type, UUID uuid) {
        this.type = type;
        this.uuid = uuid;
    }


    private UUID generateUUID() {
        UUID uuid;

        whileLoop:
        while(true) {
            uuid = UUID.randomUUID();
            for (Shrine shrine : LostShardPlugin.getShrineManager().getShrines()) {
                UUID shrineUUID = shrine.getUUID();
                if (uuid.equals(shrineUUID))
                    continue whileLoop;
            }
            return uuid;
        }

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid)
    {
        this.uuid = uuid;
    }

    public ShrineType getType() {
        return type;
    }

    public static Shrine of(ShrineType type)
    {
        switch (type)
        {
            case ATONE:
                return new AtoneShrine();
            default:
                return null;
        }
    }
}
