package com.github.kotooriiii.guards;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;

public class ShardBanker extends ShardLocationNPC {

    private static ArrayList<ShardBanker> activeShardBankers = new ArrayList<>();
    private final int socialDistance = 5;
    /**
     * Creates a new NPC with the given name and following skin.
     *
     * @param name   The name of the NPC
     */
    public ShardBanker(String name) {
        super(ChatColor.GRAY + "[Banker]", name, Skin.BANKER);
    }

    public boolean isSocialDistance(Location loc)
    {
        int xIntDiff = loc.getBlockX() - getCurrentLocation().getBlockX();
        int yIntDiff = loc.getBlockY() - getCurrentLocation().getBlockY();
        int zIntDiff = loc.getBlockZ() - getCurrentLocation().getBlockZ();
        return Math.abs(xIntDiff) <= socialDistance && Math.abs(yIntDiff) <= socialDistance && Math.abs(zIntDiff) <= socialDistance;
    }

    @Override
    public boolean spawn(int x, int y, int z, float yaw, float pitch)
    {
        if(!super.spawn(x,y,z,yaw,pitch))
            return false;
        activeShardBankers.add(this);
        return true;
    }

    @Override
    public boolean destroy()
    {
        if(!super.destroy())
            return false;
        activeShardBankers.remove(this);
        return true;
    }

    public boolean forceDestroy()
    {
        return super.destroy();
    }

    public static ArrayList<ShardBanker> getActiveShardBankers() {
        return activeShardBankers;
    }
}