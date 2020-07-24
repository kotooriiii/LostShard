package com.github.kotooriiii.status.shrine;

import org.bukkit.Location;

public class AtoneShrine extends Shrine {

    final int NEARBY_DISTANCE = 20;

    public AtoneShrine() {
        super(ShrineType.ATONE);
    }

    public boolean isNearby(Location testingLocation)
    {
        if(!this.getLocation().getWorld().equals(testingLocation.getWorld()))
            return false;
        return getLocation().distance(testingLocation) <= NEARBY_DISTANCE;
    }
}
