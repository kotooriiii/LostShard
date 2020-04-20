package com.github.kotooriiii.plots;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.hostility.Zone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.Serializable;

public class ArenaPlot extends Plot implements Serializable {

    private PointBlock spawnA;
    private PointBlock spawnB;

    public ArenaPlot(Player player, String name) {
        super(player, name);
    }

    public ArenaPlot(Zone zone, String name) {
        super(zone, name);
    }

    public Location getSpawnA() {
        return spawnA.getLocation();
    }

    public void setSpawnA(Location spawnA) {
        this.spawnA = new PointBlock(spawnA);
        FileManager.write(this);
    }

    public Location getSpawnB() {
        return spawnB.getLocation();
    }

    public void setSpawnB(Location spawnB) {
        this.spawnB = new PointBlock(spawnB);
        FileManager.write(this);
    }
}
