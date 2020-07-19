package com.github.kotooriiii.status.shrine;

import com.github.kotooriiii.files.FileManager;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ShrineManager {
    private HashSet<Shrine> map;

    public ShrineManager() {
        map = new HashSet<>();
    }

    public boolean addShrine(Shrine shrine) {
        if(isShrine(shrine.getLocation()))
            return false;
        return map.add(shrine);
    }

    public void saveShrine(Shrine shrine)
    {
        FileManager.write(shrine);
    }

    public boolean removeShrine(Location testingLocation) {
        Iterator<Shrine> iterator = map.iterator();
        while (iterator.hasNext()) {
            Shrine shrineIterator = iterator.next();

            Location location = shrineIterator.getLocation();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            World world = location.getWorld();


            if (world.equals(testingLocation.getWorld()) && x == testingLocation.getBlockX() && y == testingLocation.getBlockY() && z == testingLocation.getBlockZ()) {
                iterator.remove();
                FileManager.removeFile(shrineIterator);
                return true;
            }
        }
        return false;

    }

    public boolean isShrine(Location testingLocation) {
        Iterator<Shrine> iterator = map.iterator();
        while (iterator.hasNext()) {
            Shrine shrineIterator = iterator.next();

            Location location = shrineIterator.getLocation();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            World world = location.getWorld();


            if (world.equals(testingLocation.getWorld()) && x == testingLocation.getBlockX() && y == testingLocation.getBlockY() && z == testingLocation.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    public Shrine getShrine(Location testingLocation)
    {
        Iterator<Shrine> iterator = map.iterator();

        while (iterator.hasNext()) {
            Shrine shrineIterator = iterator.next();

            Location location = shrineIterator.getLocation();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            World world = location.getWorld();


            if (world.equals(testingLocation.getWorld()) && x == testingLocation.getBlockX() && y == testingLocation.getBlockY() && z == testingLocation.getBlockZ()) {
                return shrineIterator;
            }
        }
        return null;
    }

    public Shrine[] getShrines() {
        return map.toArray(new Shrine[0]);
    }
}
