package com.github.kotooriiii.npc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;

public class ShardBanker extends ShardLocationNPC {

    private static ArrayList<ShardBanker> activeShardBankers = new ArrayList<>();
    private final int socialDistance = 5;
    /**
     * Creates a new NPC with the given name and following skin.
     *
     * @param name   The name of the NPC
     */
    public ShardBanker(World world, String name) {
        super(world, ChatColor.GRAY + "[Banker]", name, Skin.BANKER);
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

        if (!setEquipment(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.IRON_INGOT, Material.AIR))
            return false;

        updateSpawnLocation(new Location(getCurrentLocation().getWorld(), x + 0.5, y, z + 0.5, yaw, pitch));
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

    public static ShardBanker getNearestBanker(final Location location) {
        ShardBanker shardBanker = null;
        double nearestDistance = Double.MAX_VALUE;
        for (ShardBanker banker : getActiveShardBankers()) {

            if(!banker.getCurrentLocation().getWorld().equals(location.getWorld()))
                continue;

            double distance = banker.getSpawnLocation().distance(location);
            if(distance < nearestDistance)
            {
                nearestDistance=distance;
                shardBanker=banker;
            }
        }

        return shardBanker;
    }

    public boolean forceDestroy()
    {
        return super.destroy();
    }

    public static ArrayList<ShardBanker> getActiveShardBankers() {
        return activeShardBankers;
    }
}
