package com.github.kotooriiii.sorcery;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.IBlockData;
import net.minecraft.server.v1_15_R1.WorldServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;
import sun.java2d.DisposerTarget;

import java.util.UUID;

import static net.minecraft.server.v1_15_R1.Block.getByCombinedId;

public class Gate {
    private UUID source;
    private Location from;
    private Location to;
    public final static int PORTAL_DISTANCE = 1;

    public Gate(UUID source, Location from, Location to) {
        this.source = source;
        this.from = from;
        this.to = to;
    }


    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public UUID getSource() {
        return source;
    }


    public Location getTeleportTo(GateBlock atLocation) {

        boolean isFromWorld = atLocation.getWorld().equals(from.getWorld());
        if (!isFromWorld)
            return from;
        boolean isToWorld = atLocation.getWorld().equals(to.getWorld());
        if (!isToWorld)
            return to;

        double distanceFrom = atLocation.toLocation().distance(new Location(from.getWorld(), from.getBlockX() + 0.5, from.getBlockY() + 0.5, from.getBlockZ() + 0.5));
        double distanceTo = atLocation.toLocation().distance(new Location(to.getWorld(), to.getBlockX() + 0.5, to.getBlockY() + 0.5, to.getBlockZ() + 0.5));


        if (isFromWorld && isToWorld) {
            if (distanceFrom < distanceTo)
                return to;
            else
                return from;
        } else {
            return null;
        }
    }

    public void build() {
        Block fromFeet = from.getBlock();
        Block fromHead = fromFeet.getRelative(BlockFace.UP);

        Block toFeet = to.getBlock();
        Block toHead = toFeet.getRelative(BlockFace.UP);

        fromFeet.setType(Material.NETHER_PORTAL);
        fromHead.setType(Material.NETHER_PORTAL);
        toFeet.setType(Material.NETHER_PORTAL);
        toHead.setType(Material.NETHER_PORTAL);


    }

    public boolean isBuilt() {
        Block fromFeet = from.getBlock();
        Block fromHead = fromFeet.getRelative(BlockFace.UP);

        Block toFeet = to.getBlock();
        Block toHead = toFeet.getRelative(BlockFace.UP);

        return fromFeet.getType() == fromHead.getType() && fromHead.getType() == toFeet.getType() && toFeet.getType() == toHead.getType() && toHead.getType() == Material.NETHER_PORTAL;
    }

    public boolean isBuildable() {
        Block fromFeet = from.getBlock();
        Block fromHead = fromFeet.getRelative(BlockFace.UP);

        Block toFeet = to.getBlock();
        Block toHead = toFeet.getRelative(BlockFace.UP);
        return isAir(fromFeet) && isAir(fromHead) && isAir(toFeet) && isAir(toHead);
    }

    public boolean isAir(Block b) {
        return b.getType().isAir();
    }

    public static void setBlockInNativeWorld(World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics) {
        final WorldServer handle = ((CraftWorld) world).getHandle();
        BlockPosition bp = new BlockPosition(x, y, z);
        IBlockData ibd = getByCombinedId(blockId + (data << 12));
        handle.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
    }

    public static void setBlockInNativeWorld(World world, int x, int y, int z, Material type, boolean applyPhysics) {
        final WorldServer handle = ((CraftWorld) world).getHandle();
        BlockPosition bp = new BlockPosition(x, y, z);
        IBlockData ibd = ((CraftBlockData) Bukkit.createBlockData(type)).getState();

        handle.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
    }


    public void destroy() {
        Block fromFeet = from.getBlock();

        final float OFFSET = 0.5f;
        final int AMOUNT = 60;
        final Particle PARTICLE = Particle.DRAGON_BREATH;
        final int VOLUME = 6;
        final float PITCH = 1.5f;


        if (fromFeet.getType() == Material.NETHER_PORTAL) {
            //setBlockInNativeWorld(fromFeet.getWorld(), fromFeet.getLocation().getBlockX(), fromFeet.getLocation().getBlockY(), fromFeet.getLocation().getBlockZ(), 0, (byte) 0, Material.AIR.hasGravity());
            fromFeet.breakNaturally();
            fromFeet.getWorld().playSound(fromFeet.getLocation(), Sound.BLOCK_GLASS_BREAK, VOLUME, PITCH);
            fromFeet.getWorld().spawnParticle(PARTICLE, fromFeet.getLocation(), AMOUNT, OFFSET, OFFSET, OFFSET);
        }

        Block fromHead = fromFeet.getRelative(BlockFace.UP);
        if (fromHead.getType() == Material.NETHER_PORTAL) {
            //   setBlockInNativeWorld(fromHead.getWorld(), fromHead.getLocation().getBlockX(), fromHead.getLocation().getBlockY(), fromHead.getLocation().getBlockZ(), 0, (byte) 0, Material.AIR.hasGravity());
            fromHead.breakNaturally();
            fromHead.getWorld().playSound(fromHead.getLocation(), Sound.BLOCK_GLASS_BREAK, VOLUME, PITCH);
            fromHead.getWorld().spawnParticle(PARTICLE, fromHead.getLocation(),  AMOUNT, OFFSET, OFFSET, OFFSET);
        }

        Block toFeet = to.getBlock();
        if (toFeet.getType() == Material.NETHER_PORTAL) {
            //   setBlockInNativeWorld(toFeet.getWorld(), toFeet.getLocation().getBlockX(), toFeet.getLocation().getBlockY(), toFeet.getLocation().getBlockZ(), 0, (byte) 0, Material.AIR.hasGravity());
            toFeet.breakNaturally();
            toFeet.getWorld().playSound(toFeet.getLocation(), Sound.BLOCK_GLASS_BREAK, VOLUME, PITCH);
            toFeet.getWorld().spawnParticle(PARTICLE, toFeet.getLocation(),  AMOUNT, OFFSET, OFFSET, OFFSET);
        }

        Block toHead = toFeet.getRelative(BlockFace.UP);
        if (toHead.getType() == Material.NETHER_PORTAL) {
         //   setBlockInNativeWorld(toHead.getWorld(), toHead.getLocation().getBlockX(), toHead.getLocation().getBlockY(), toHead.getLocation().getBlockZ(), 0, (byte) 0, Material.AIR.hasGravity());
            toHead.breakNaturally();
            toHead.getWorld().playSound(toHead.getLocation(), Sound.BLOCK_GLASS_BREAK, VOLUME, PITCH);
            toHead.getWorld().spawnParticle(PARTICLE, toHead.getLocation(),  AMOUNT, OFFSET, OFFSET, OFFSET);
        }
    }

    @Override
    public String toString() {
        return "From: " + "\n" + from + "\nTo: " + "\n" + to;
    }


    public void rotate(Block block) {
        Location testingLoc = block.getLocation();
        Location sourceLoc = getTeleportTo(new GateBlock(getTeleportTo(new GateBlock(testingLoc))));

        if (sourceLoc.getBlock().getType() != Material.NETHER_PORTAL)
            return;

        Block bottom = sourceLoc.getBlock();
        Block top = bottom.getRelative(BlockFace.UP);


        Orientable orientableBottom = (Orientable) bottom.getBlockData();
        Orientable orientableTop = (Orientable) top.getBlockData();

        Axis currentAxis = orientableBottom.getAxis();
        Axis switchTo;


        if (currentAxis == Axis.X)
            switchTo = Axis.Z;
        else
            switchTo = Axis.X;

        orientableBottom.setAxis(switchTo);
        orientableTop.setAxis(switchTo);
        bottom.setBlockData(orientableBottom);
        top.setBlockData(orientableTop);

    }
}
