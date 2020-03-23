package com.github.kotooriiii.hostility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.Serializable;

public class HostilityZone implements Serializable {

    private static final long serialVersionUID = 1L;


    private int x1;
    private int x2;

    private int y1;
    private int y2;

    private int z1;
    private int z2;


    public HostilityZone(int x1, int y1, int z1)
    {
        this.x1 = x1;
        this.x2 = x1;

        this.y1 = y1;
        this.y2= y1;

        this.z1 = z1;
        this.z2 = z1;
    }

    public HostilityZone(Location loc1)
    {
        this.x1 = loc1.getBlockX();
        this.x2 = loc1.getBlockX();

        this.y1 = loc1.getBlockY();
        this.y2 = loc1.getBlockY();

        this.z1 = loc1.getBlockZ();
        this.z2 = loc1.getBlockZ(); }


    public HostilityZone(int x1, int x2, int y1, int y2, int z1, int z2)
    {
        this.x1 = x1;
        this.x2 = x2;

        this.y1 = y1;
        this.y2 = y2;

        this.z1 = z1;
        this.z2 = z2;

        clean();
    }

    public HostilityZone(Location loc1, Location loc2)
    {
        this.x1 = loc1.getBlockX();
        this.x2 = loc2.getBlockX();

        this.y1 = loc1.getBlockY();
        this.y2 = loc2.getBlockY();

        this.z1 = loc1.getBlockZ();
        this.z2 = loc2.getBlockZ();

      clean();
    }

    public boolean hasAdjacency(Location location)
    {
        int locX = location.getBlockX();
        int locY = location.getBlockY();
        int locZ = location.getBlockZ();

        int tempX1 = this.x1-1;
        int tempX2 = this.x2+1;

        int tempY1 = this.y1-1;
        int tempY2 = this.y2+1;

        int tempZ1 = this.z1-1;
        int tempZ2 = this.z2+1;


        if(tempX1 <= locX && locX <= tempX2)
        {
            if(tempZ1 <= locZ && locZ <= tempZ2)
            {
                if(tempY1 <= locY && locY <= tempY2)
                    return true;
            }
        }
        return false;
    }

    public boolean contains(int x, int y, int z)
    {
        if(x1 <= x && x <= x2)
        {
            if(z1 <= z && z <= z2)
            {
                if(y1 <= y && y <= y2)
                return true;
            }
        }
        return false;
    }

    public boolean contains(Block block)
    {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        if(x1 <= x && x <= x2)
        {
            if(z1 <= z && z <= z2)
            {
                if(y1 <= y && y<=y2)
                return true;
            }
        }
        return false;
    }

    private void clean()
    {
        int x1Temp = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);

        int y1Temp = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);

        int z1Temp = Math.min(z1,z2);
        this.z2 = Math.max(z1,z2);

        this.x1 = x1Temp;
        this.y1=  y1Temp;
        this.z1 = z1Temp;
    }

    public String toString()
    {
        return "[x1:" + this.x1 + "x2:" + this.x2 + "][y1:" + y1 + "y2:" + this.y2 + "][z1:"  + z1 + "z2:" + z2+"]";
    }
}
