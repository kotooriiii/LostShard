package com.github.kotooriiii.sorcery;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class GateBlock {

    private World world;
    private int x;
    private int y;
    private int z;

    public GateBlock(World w, int x, int y, int z) {
        this.world = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GateBlock(Location location) {
        this.world = location.getWorld();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Location toLocation()
    {
        return new Location(getWorld(), getX(), getY(), getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GateBlock gateBlock = (GateBlock) o;
        return getX() == gateBlock.getX() &&
                getY() == gateBlock.getY() &&
                getZ() == gateBlock.getZ() &&
                getWorld().equals(gateBlock.getWorld());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorld(), getX(), getY(), getZ());
    }

}
