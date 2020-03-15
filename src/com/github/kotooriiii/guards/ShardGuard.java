package com.github.kotooriiii.guards;

import com.github.kotooriiii.LostShardK;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShardGuard extends ShardLocationNPC {

    static public ArrayList<ShardGuard> activeShardGuards = new ArrayList<>();

    private final int warningRadius = 4;
    private final int alertRadius = warningRadius - 2;

    private boolean isBusy = false;


    private List<org.bukkit.block.Block> boundingBlocks = new ArrayList<>();

    private BukkitTask task;

    public ShardGuard(String name) {

        super(ChatColor.GRAY + "[" + "Guard" + "]", name, Skin.GUARD);
        //Set as active Guard
    }

    /**
     * Spawns the Guard on given Location
     *
     * @param location The given location for the Guard to spawn
     */
    @Override
    public boolean spawn(Location location) {
        return this.spawn(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Spawns the Guard on the given parameters
     *
     * @param x     The block on the x coordinate
     * @param y     The block on the y coordinate
     * @param z     The block on the z coordinate
     * @param yaw   The yaw of the guard
     * @param pitch The pitch of the guard
     */
    @Override
    public boolean spawn(int x, int y, int z, float yaw, float pitch) {

        if (!super.spawn(x, y, z, yaw, pitch))
            return false;

        getActiveShardGuards().add(this);

        if (!setEquipment(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.IRON_SWORD, Material.SHIELD))
            return false;

        //Update location, remember the world is here already!
        updateLocation(new Location(getCurrentLocation().getWorld(), x + 0.5, y, z + 0.5, yaw, pitch));
        showBounds();
        observe();
        return true;
    }

    /**
     * Destroys the Guard.
     */
    @Override
    public boolean destroy() {
        if(!super.destroy())
            return false;
        //Clear last ones
      clearBounds();

        if (task != null)
            task.cancel();

        getActiveShardGuards().remove(this); //Remove as active guard
        return true;
    }

    /**
     * Destroys the Guard.
     */
    public boolean forceDestroy() {
        if(!super.destroy())
            return false;
        //Clear last ones
        clearBounds();

        if (task != null)
            task.cancel();

        return true;
    }

    /**
     * Teleports the Guard to desired location.
     *
     * @param location The location where the Guard will teleport to.
     */
    @Override
    public boolean teleport(Location location) {

        if (!super.teleport(location))
            return false;
        showBounds();
        return true;
    }

    /**
     * Teleports the Guard to desired location. The player in in the parameter will be killed in consequence.
     *
     * @param player The player being killed.
     * @param loc    The location being teleported to.
     */
    private boolean teleportKill(final Player player, Location loc) {
        if (isDestroyed() || !isSpawned())
            return false;
        //Get the world
        World w = player.getWorld();
        //Play the enderman sound
        w.playSound(loc, Sound.ENTITY_ENDERMAN_SCREAM, 10, 0);
        //Spawn particle at location
        w.spawnParticle(Particle.FIREWORKS_SPARK, getCurrentLocation(), 20, 0.3, 0.3, 0.3);
        //Force player to look at Guard
        lookAtThis(player);
        //Slow player
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15, 127, false, false, false));
        //Get location of player in case of logging out
        final Location playerLocation = player.getLocation();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isDestroyed())
                    return;
                teleport(loc);
                w.spawnParticle(Particle.CRIT, playerLocation, 50, 0.3, 0.3, 0.3);
                w.spawnParticle(Particle.DRAGON_BREATH, playerLocation, 50, 2, 2, 2);

                w.playSound(playerLocation, Sound.ENTITY_DROWNED_DEATH, 10, 0);
                if (player.isOnline())
                    player.setHealth(1);
            }
        }.runTaskLater(LostShardK.plugin, 15);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isDestroyed())
                    return;
                w.playSound(playerLocation, Sound.ENTITY_PLAYER_BURP, 10, 0);
            }
        }.runTaskLater(LostShardK.plugin, 35);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isDestroyed())
                    return;
                isBusy = false;
                teleport(getSpawnLocation());
                observe();
            }
        }.runTaskLater(LostShardK.plugin, 70);
        return true;
    }

    private List<org.bukkit.block.Block> getAlertBlocks() {
        List<org.bukkit.block.Block> blocks = new ArrayList<>();

        for (int x = getCurrentLocation().getBlockX() - alertRadius; x <= getCurrentLocation().getBlockX() + alertRadius; x++) {
            for (int z = getCurrentLocation().getBlockZ() - alertRadius; z <= getCurrentLocation().getBlockZ() + alertRadius; z++) {
                yLoop:
                for (int y = getCurrentLocation().getBlockY() + 3; y >= warningRadius; y--) {
                    Block currentBlock = new Location(getCurrentLocation().getWorld(), x, y, z).getBlock();
                    Block blockBelow = new Location(getCurrentLocation().getWorld(), x, y - 1, z).getBlock();

                    if (currentBlock.getType().equals(Material.AIR) && !blockBelow.getType().equals(Material.AIR)) {
                        blocks.add(currentBlock);
                        break yLoop;
                    }
                }
            }
        }

        return blocks;
    }

    private List<org.bukkit.block.Block> getWarningBlocks() {
        List<org.bukkit.block.Block> blocks = new ArrayList<>();

        //top
        for (int x = getCurrentLocation().getBlockX() - warningRadius; x <= getCurrentLocation().getBlockX() + warningRadius; x++) {
            for (int z = getCurrentLocation().getBlockZ() + alertRadius + 1; z <= getCurrentLocation().getBlockZ() + warningRadius; z++) {
                yLoop:
                for (int y = getCurrentLocation().getBlockY() + 3; y >= warningRadius; y--) {
                    Block currentBlock = new Location(getCurrentLocation().getWorld(), x, y, z).getBlock();
                    Block blockBelow = new Location(getCurrentLocation().getWorld(), x, y - 1, z).getBlock();

                    if (currentBlock.getType().equals(Material.AIR) && !blockBelow.getType().equals(Material.AIR)) {
                        blocks.add(currentBlock);
                        break yLoop;
                    }
                }
            }
        }


        //left block
        for (int x = getCurrentLocation().getBlockX() - warningRadius; x < getCurrentLocation().getBlockX() - alertRadius; x++) {
            for (int z = getCurrentLocation().getBlockZ() - alertRadius; z <= getCurrentLocation().getBlockZ() + alertRadius; z++) {
                yLoop:
                for (int y = getCurrentLocation().getBlockY() + 3; y >= warningRadius; y--) {
                    Block currentBlock = new Location(getCurrentLocation().getWorld(), x, y, z).getBlock();
                    Block blockBelow = new Location(getCurrentLocation().getWorld(), x, y - 1, z).getBlock();

                    if (currentBlock.getType().equals(Material.AIR) && !blockBelow.getType().equals(Material.AIR)) {
                        blocks.add(currentBlock);
                        break yLoop;
                    }
                }
            }
        }

        //right block
        for (int x = getCurrentLocation().getBlockX() + alertRadius + 1; x <= getCurrentLocation().getBlockX() + warningRadius; x++) {
            for (int z = getCurrentLocation().getBlockZ() - alertRadius; z <= getCurrentLocation().getBlockZ() + alertRadius; z++) {
                yLoop:
                for (int y = getCurrentLocation().getBlockY() + 3; y >= warningRadius; y--) {
                    Block currentBlock = new Location(getCurrentLocation().getWorld(), x, y, z).getBlock();
                    Block blockBelow = new Location(getCurrentLocation().getWorld(), x, y - 1, z).getBlock();

                    if (currentBlock.getType().equals(Material.AIR) && !blockBelow.getType().equals(Material.AIR)) {
                        blocks.add(currentBlock);
                        break yLoop;
                    }
                }
            }
        }

        for (int x = getCurrentLocation().getBlockX() - warningRadius; x <= getCurrentLocation().getBlockX() + warningRadius; x++) {
            for (int z = getCurrentLocation().getBlockZ() - warningRadius; z < getCurrentLocation().getBlockZ() - alertRadius; z++) {
                yLoop:
                for (int y = getCurrentLocation().getBlockY() + 3; y >= warningRadius; y--) {
                    Block currentBlock = new Location(getCurrentLocation().getWorld(), x, y, z).getBlock();
                    Block blockBelow = new Location(getCurrentLocation().getWorld(), x, y - 1, z).getBlock();

                    if (currentBlock.getType().equals(Material.AIR) && !blockBelow.getType().equals(Material.AIR)) {
                        blocks.add(currentBlock);
                        break yLoop;
                    }
                }
            }
        }

        return blocks;
    }


    private void clearBounds() {
        //Clear last ones
        for (Block block : boundingBlocks) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
            }
        }
    }

    private void showBounds() {

        clearBounds();

        List<org.bukkit.block.Block> alertingBlocks = getAlertBlocks();
        List<org.bukkit.block.Block> warningBlocks = getWarningBlocks();

        List<org.bukkit.block.Block> all = new ArrayList<>();
        all.addAll(getAlertBlocks());
        all.addAll(getWarningBlocks());
        boundingBlocks = all;

        for (Player players : Bukkit.getOnlinePlayers()) {
            for (Block block : (alertingBlocks)) {
                double ran = Math.random();
                if (ran <= 0.20)
                    players.spawnParticle(Particle.FLAME, block.getX(), block.getY(), block.getZ(), 1);
                players.sendBlockChange(block.getLocation(), Material.RED_CARPET.createBlockData());

            }

            for (Block block : warningBlocks) {
                // players.spawnParticle(Particle.TOTEM, block.getX(), block.getY(), block.getZ(), 1);
                players.sendBlockChange(block.getLocation(), Material.YELLOW_CARPET.createBlockData());
            }
        }
    }

    /**
     * Observes the behavior of players around the warning radius.
     */
    private void observe() {
        task = new BukkitRunnable() {
            @Override
            public void run() {

                for (org.bukkit.entity.Entity entity : getCurrentLocation().getWorld().getNearbyEntities(getCurrentLocation(), warningRadius, warningRadius, warningRadius)) {
                    if (entity instanceof Player) {
                        if (!"is not in order".isEmpty()) {
                            Player player = (Player) entity;

                            Location loc = entity.getLocation();

                            double xDiff = loc.getX() - getCurrentLocation().getX();
                            double yDiff = loc.getY() - getCurrentLocation().getY();
                            double zDiff = loc.getZ() - getCurrentLocation().getZ();

                            int xIntDiff = loc.getBlockX() - getCurrentLocation().getBlockX();
                            int yIntDiff = loc.getBlockY() - getCurrentLocation().getBlockY();
                            int zIntDiff = loc.getBlockZ() - getCurrentLocation().getBlockZ();

                            double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
                            double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
                            double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI;
                            double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
                            if (zDiff < 0.0)
                                newYaw = newYaw + Math.abs(180 - newYaw) * 2;
                            newYaw = (newYaw - 90);

                            float yaw = (float) newYaw;
                            float pitch = (float) newPitch;

                            if (Math.abs(xIntDiff) <= alertRadius && Math.abs(yIntDiff) <= alertRadius && Math.abs(zIntDiff) <= alertRadius) {
                                isBusy = true;
                                teleportKill(player, new Location(entity.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), yaw, pitch));
                                this.cancel();
                                return;
                            } else {
                                rotateHead(yaw, pitch);
                                int randomInt = new Random().nextInt(3);
                                double randomChance = Math.random();
                                if (randomInt != 0) {
                                    if (randomChance <= 0.045)
                                        player.spawnParticle(Particle.VILLAGER_ANGRY, getCurrentLocation().getBlockX() + 0.5, getCurrentLocation().getBlockY() + 2, getCurrentLocation().getBlockZ() + 0.5, randomInt, 0.25, 0, 0.25);
                                    player.spawnParticle(Particle.SMOKE_NORMAL, getCurrentLocation().getBlockX() + 0.5, getCurrentLocation().getBlockY() + 1, getCurrentLocation().getBlockZ() + 0.5, 1, 0, 0, 0);
                                }

                            }
                        }
                    }
                }

            }
        }.runTaskTimer(LostShardK.plugin, 5, 1);
    }

    //BASIC GETTER/SETTER

    public static ShardGuard getNearestGuard(final Location location) {
        ShardGuard nearestGuard = null;
        double nearestDistance = -1;
        for (ShardGuard guard : getActiveShardGuards()) {
            if(guard.isBusy())
                continue;
            double distance = guard.getSpawnLocation().distance(location);
            if(distance < nearestDistance)
            {
                nearestDistance=distance;
                nearestGuard=guard;
            }
        }

        return nearestGuard;
    }

    public static ArrayList<ShardGuard> getActiveShardGuards() {
        return activeShardGuards;
    }

    public boolean isBusy() {
        return isBusy;
    }
}
