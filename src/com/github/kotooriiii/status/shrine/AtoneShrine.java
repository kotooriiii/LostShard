package com.github.kotooriiii.status.shrine;

import org.bukkit.Location;

public class AtoneShrine extends Shrine {

    final int NEARBY_DISTANCE = 5;

    public AtoneShrine() {
        super();
    }

    public boolean isNearby(Location testingLocation)
    {
        return getLocation().distance(testingLocation) <= 5;
    }
}
