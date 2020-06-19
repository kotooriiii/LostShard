package com.github.kotooriiii.sorcery.spells;

import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class Gate {
    private Player source;
    private MarkPlayer.Mark mark;
    private Location from;
    private Location to;

    public Gate(Player source, MarkPlayer.Mark mark, Location from, Location to) {
        this.source = source;
        this.mark = mark;
        this.from = from;
        this.to = to;
    }

    public MarkPlayer.Mark getMark() {
        return mark;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public Player getSource() {
        return source;
    }

    public void build()
    {
        Block fromFeet = from.getBlock();
        Block fromHead = fromFeet.getRelative(BlockFace.UP);

        Block toFeet = to.getBlock();
        Block toHead = toFeet.getRelative(BlockFace.UP);

        fromFeet.setType(Material.NETHER_PORTAL);
        fromHead.setType(Material.NETHER_PORTAL);
        toFeet.setType(Material.NETHER_PORTAL);
        toHead.setType(Material.NETHER_PORTAL);
    }

    public boolean isBuildable()
    {
        Block fromFeet = from.getBlock();
        Block fromHead = fromFeet.getRelative(BlockFace.UP);

        Block toFeet = to.getBlock();
        Block toHead = toFeet.getRelative(BlockFace.UP);
        return fromFeet.isEmpty() && fromHead.isEmpty() && toFeet.isEmpty() && toHead.isEmpty();
    }

    public boolean isGateBlock(Block block) {
        return isGateBlock(block.getLocation());
    }

    private boolean isGateBlock(Location location) {

        int cX = location.getBlockX();
        int cY = location.getBlockY();
        int cZ = location.getBlockZ();

        int fX = from.getBlockX();
        int fY = from.getBlockY();
        int fZ = from.getBlockZ();

        Block headFrom = from.getBlock().getRelative(BlockFace.UP);
        Location headFromLoc = headFrom.getLocation();

        int headFromX = headFromLoc.getBlockX();
        int headFromY = headFromLoc.getBlockY();
        int headFromZ = headFromLoc.getBlockZ();

        if (location.getWorld().equals(from.getWorld())) {
            return (fX == cX && fY == cY && fZ == cZ) || (headFromX == cX && headFromY == cY && headFromZ == cZ);
        }

        int tX = to.getBlockX();
        int tY = to.getBlockY();
        int tZ = to.getBlockZ();

        Block headTo = to.getBlock().getRelative(BlockFace.UP);
        Location headToLoc = headTo.getLocation();

        int headToX = headToLoc.getBlockX();
        int headToY = headToLoc.getBlockY();
        int headToZ = headToLoc.getBlockZ();

        if(location.getWorld().equals(to.getWorld()))
        {
            return (tX == cX && tY == cY && tZ == cZ )|| (headToX == cX && headToY == cY && headToZ == cZ);
        }


        return false;
    }

    public void remove() {
        Block fromFeet = from.getBlock();
        if(fromFeet.getType() == Material.NETHER_PORTAL)
            fromFeet.setType(Material.AIR);
        Block fromHead = fromFeet.getRelative(BlockFace.UP);
        if(fromHead.getType() == Material.NETHER_PORTAL)
            fromHead.setType(Material.AIR);
        Block toFeet = to.getBlock();
        if(toFeet.getType() == Material.NETHER_PORTAL)
            toFeet.setType(Material.AIR);
        Block toHead = toFeet.getRelative(BlockFace.UP);
        if(toHead.getType() == Material.NETHER_PORTAL)
            toHead.setType(Material.AIR);
    }
}
