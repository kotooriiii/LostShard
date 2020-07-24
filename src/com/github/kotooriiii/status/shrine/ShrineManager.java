package com.github.kotooriiii.status.shrine;

import com.github.kotooriiii.files.FileManager;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class ShrineManager {
    private ArrayList<Shrine> list;

    public ShrineManager() {
        list = new ArrayList<>();
    }

    public boolean addShrine(Shrine shrine, boolean saveToFile) {
        if (isShrine(shrine.getLocation()))
            return false;

        boolean hasSaved = list.add(shrine);

        if (saveToFile && hasSaved)
            saveShrine(shrine);
        return hasSaved && hasSaved;
    }

    public void saveShrine(Shrine shrine) {
        FileManager.write(shrine);
    }

    public boolean removeShrine(Location testingLocation) {
        Iterator<Shrine> iterator = list.iterator();
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

    public boolean removeShrine(Shrine shrine) {
        return list.remove(shrine);

    }

    public boolean isShrine(Location testingLocation) {
        Iterator<Shrine> iterator = list.iterator();
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

    public Shrine getShrine(Location testingLocation) {
        Iterator<Shrine> iterator = list.iterator();

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

    public Shrine getShrine(UUID uuid) {
        Iterator<Shrine> iterator = list.iterator();

        while (iterator.hasNext()) {
            Shrine shrineIterator = iterator.next();
            if(shrineIterator.getUUID().equals(uuid))
                return shrineIterator;
        }
        return null;
    }

    public Shrine[] getShrines() {
        return list.toArray(new Shrine[0]);
    }

    public TreeMap<String, Shrine> getMap() {
        TreeMap<String, Shrine> map = new TreeMap<>();
        for (Shrine shrine : getShrines())
            map.put(shrine.getType().name(), shrine);
        return map;
    }

}
