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
import org.bukkit.entity.Player;

import java.util.UUID;

public class Gate {
    private UUID source;
    private Location from;
    private Location to;
    public final static int PORTAL_DISTANCE = 2;

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


    public Location getTeleportTo(GateBlock fromLocation) {


        if (fromLocation.toLocation().distance(from) < 1.25f) {

            return to;

        } else if (fromLocation.toLocation().distance(to) < 1.25f) {
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

    public boolean isBuildable() {
        Block fromFeet = from.getBlock();
        Block fromHead = fromFeet.getRelative(BlockFace.UP);

        Block toFeet = to.getBlock();
        Block toHead = toFeet.getRelative(BlockFace.UP);
        return isAir(fromFeet) && isAir(fromHead) && isAir(toFeet) && isAir(toHead);
    }

    public boolean isAir(Block b)
    {return b.getType().isAir();}


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


}
