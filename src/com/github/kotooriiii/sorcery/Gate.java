package com.github.kotooriiii.sorcery;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import sun.java2d.DisposerTarget;

import java.util.UUID;

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
        if(!isFromWorld)
            return from;
        boolean isToWorld = atLocation.getWorld().equals(to.getWorld());
        if(!isToWorld)
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


    public void destroy() {
        Block fromFeet = from.getBlock();
        if (fromFeet.getType() == Material.NETHER_PORTAL)
            fromFeet.setType(Material.AIR);
        Block fromHead = fromFeet.getRelative(BlockFace.UP);
        if (fromHead.getType() == Material.NETHER_PORTAL)
            fromHead.setType(Material.AIR);
        Block toFeet = to.getBlock();
        if (toFeet.getType() == Material.NETHER_PORTAL)
            toFeet.setType(Material.AIR);
        Block toHead = toFeet.getRelative(BlockFace.UP);
        if (toHead.getType() == Material.NETHER_PORTAL)
            toHead.setType(Material.AIR);
    }

    @Override
    public String toString()
    {
        return "From: " + "\n" +from + "\nTo: " + "\n" + to;
    }


}
