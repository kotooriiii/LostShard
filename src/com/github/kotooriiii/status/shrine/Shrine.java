package com.github.kotooriiii.status.shrine;


import org.bukkit.Location;

public  abstract class Shrine {
    private Location location;
    private ShrineType type;

    public Shrine(ShrineType type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
