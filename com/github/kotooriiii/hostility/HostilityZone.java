package com.github.kotooriiii.hostility;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class HostilityZone {
    private int x1;
    private int x2;

    private int z1;
    private int z2;


    public HostilityZone(int x1, int z1)
    {
        this.x1 = x1;
        this.x2 = x1;

        this.z1 = z1;
        this.z2 = z1;
    }

    public HostilityZone(Location loc1)
    {
        this.x1 = loc1.getBlockX();
        this.x2 = loc1.getBlockX();

        this.z1 = loc1.getBlockZ();
        this.z2 = loc1.getBlockZ(); }


    public HostilityZone(int x1, int x2, int z1, int z2)
    {
        this.x1 = x1;
        this.x2 = x2;

        this.z1 = z1;
        this.z2 = z2;

        clean();
    }

    public HostilityZone(Location loc1, Location loc2)
    {
        this.x1 = loc1.getBlockX();
        this.x2 = loc2.getBlockX();

        this.z1 = loc1.getBlockZ();
        this.z2 = loc2.getBlockZ();

        clean();
    }

    public boolean contains(int x, int z)
    {
        if(x1 <= x && x <= x2)
        {
            if(z1 <= z && z <= z2)
            {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Block block)
    {
        int x = block.getX();
        int z = block.getZ();

        if(x1 <= x && x <= x2)
        {
            if(z1 <= z && z <= z2)
            {
                return true;
            }
        }
        return false;
    }

    private void clean()
    {
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);

        this.z1 = Math.min(z1,z2);
        this.z2 = Math.max(z1,z2);
    }
}
