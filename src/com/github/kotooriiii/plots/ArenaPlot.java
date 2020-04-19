package com.github.kotooriiii.plots;

import com.github.kotooriiii.hostility.Zone;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.Serializable;

public class ArenaPlot extends Plot implements Serializable {

    private Location spawnA;
    private Location spawnB;

    public ArenaPlot(Player player, String name) {
        super(player, name);
    }

    public ArenaPlot(Zone zone, String name) {
        super(zone, name);
    }

    public Location getSpawnA() {
        return spawnA;
    }

    public void setSpawnA(Location spawnA) {
        this.spawnA = spawnA;
    }

    public Location getSpawnB() {
        return spawnB;
    }

    public void setSpawnB(Location spawnB) {
        this.spawnB = spawnB;
    }
}
