package com.github.kotooriiii.hostility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.Serializable;

public class Zone implements Serializable {

    private static final long serialVersionUID = 1L;


    private int x1;
    private int x2;

    private int y1;
    private int y2;

    private int z1;
    private int z2;


    public Zone(int x1, int x2, int y1, int y2, int z1, int z2) {
        this.x1 = x1;
        this.x2 = x2;

        this.y1 = y1;
        this.y2 = y2;

        this.z1 = z1;
        this.z2 = z2;

        clean();
    }

    public Zone(Location loc1, Location loc2) {
        this.x1 = loc1.getBlockX();
        this.x2 = loc2.getBlockX();

        this.y1 = loc1.getBlockY();
        this.y2 = loc2.getBlockY();

        this.z1 = loc1.getBlockZ();
        this.z2 = loc2.getBlockZ();

        clean();
    }

    public boolean hasAdjacency(Location location) {
        int locX = location.getBlockX();
        int locY = location.getBlockY();
        int locZ = location.getBlockZ();

        int tempX1 = this.x1 - 1;
        int tempX2 = this.x2 + 1;

        int tempY1 = this.y1 - 1;
        int tempY2 = this.y2 + 1;

        int tempZ1 = this.z1 - 1;
        int tempZ2 = this.z2 + 1;


        if (tempX1 <= locX && locX <= tempX2) {
            if (tempZ1 <= locZ && locZ <= tempZ2) {
                if (tempY1 <= locY && locY <= tempY2)
                    return true;
            }
        }
        return false;
    }

    public boolean overlaps(Zone zone) {
        int otherZoneX1 = zone.x1;
        int otherZoneX2 = zone.x2;

        int otherZoneY1 = zone.y1;
        int otherZoneY2 = zone.y2;

        int otherZoneZ1 = zone.z1;
        int otherZoneZ2 = zone.z2;

        if (this.x2 < otherZoneX1) {
            // this.x2 |  NON-OVERLAP  | other x1
            return false;
        }

        if (otherZoneX2 < this.x1) {
            // other X2 | non overlap | this.x1
            return false;
        }

        if (this.y2 < otherZoneY1) {
            // other y1
            // ------
            //this y2
            return false;
        }

        if (otherZoneY2 < this.y1) {
            // this y1
            // ----
            // other y2
            return false;
        }

        if (this.z2 < otherZoneZ1) {
            // this.z2 |  NON-OVERLAP  | other z1
            return false;
        }

        if (otherZoneZ2 < this.z1) {
            // other Z2 | non overlap | this.z1
            return false;
        }

        return true;
    }

    public boolean contains(int x, int y, int z) {
        if (x1 <= x && x <= x2) {
            if (z1 <= z && z <= z2) {
                if (y1 <= y && y <= y2)
                    return true;
            }
        }
        return false;
    }

    public boolean contains(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        if (x1 <= x && x <= x2) {
            if (z1 <= z && z <= z2) {
                if (y1 <= y && y <= y2)
                    return true;
            }
        }
        return false;
    }

    public boolean contains(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        if (x1 <= x && x <= x2) {
            if (z1 <= z && z <= z2) {
                if (y1 <= y && y <= y2)
                    return true;
            }
        }
        return false;
    }

    private void clean() {
        int x1Temp = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);

        int y1Temp = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);

        int z1Temp = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);

        this.x1 = x1Temp;
        this.y1 = y1Temp;
        this.z1 = z1Temp;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public int getZ1() {
        return z1;
    }

    public int getZ2() {
        return z2;
    }

    public String toString() {
        return "[x1:" + this.x1 + "x2:" + this.x2 + "][y1:" + y1 + "y2:" + this.y2 + "][z1:" + z1 + "z2:" + z2 + "]";
    }
}
