package com.github.kotooriiii.status;

import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;

public class ShrineManager {
    private HashMap<Location, Shrine> map;

    public ShrineManager()
    {
        map = new HashMap<>();
    }

    public void addShrine(Shrine shrine){

    }

    public void removeShrine(Shrine shrine)
    {

    }

    public boolean isShrine(Location location)
    {

    }

    public boolean isNearbyShrine()
    {
        final int DISTANCE = 5;
    }

    public Collection<Shrine> getShrines()
    {
        return  map.values();
    }
}
