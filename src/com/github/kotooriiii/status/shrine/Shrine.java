package com.github.kotooriiii.status.shrine;


import org.bukkit.Location;

public  abstract class Shrine {
    private Location location;

//    public Shrine(Location location) {
//        this.location = location;
//    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
