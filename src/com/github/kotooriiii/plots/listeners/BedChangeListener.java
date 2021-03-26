package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.plots.struct.SpawnPlot;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class BedChangeListener implements Listener {
    @EventHandler
    public void onPlaceSign(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null)
            return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (!isBed(block))
            return;
        event.setCancelled(true);
        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(block.getLocation());
        if (plot == null)
            return;

        if (!(plot.getClass().equals(PlayerPlot.class)))
            return;
        PlayerPlot playerPlot = (PlayerPlot) plot;
        if (!playerPlot.isTown())
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        if (!playerPlot.hasPermissionToUse(uuid)) {
            event.getPlayer().sendMessage(ERROR_COLOR + "You do not have permission to set your spawn location here.");
            return;
        }
        event.getPlayer().sendMessage(ChatColor.GOLD + "Your spawnpoint has been set to the Town \"" + playerPlot.getName() + "\".");
        saveSpawn(event.getPlayer(), block.getLocation());
    }

    @EventHandler
    public void onRemove(EntityExplodeEvent event) {

        for (Block block : event.blockList()) {
            if (block == null)
                continue;
            if (isBed(block)) {
                Block head = getHeadOfBed(block);
                if (head == null)
                    continue;
                removeSpawn(head.getLocation());
            }
        }
    }

    @EventHandler
    public void onRemove(BlockBurnEvent event) {

        Block block = event.getBlock();
        if (block == null)
            return;
        if (isBed(block)) {
            Block head = getHeadOfBed(block);
            if (head == null)
                return;
            removeSpawn(head.getLocation());
        }
    }

    @EventHandler
    public void onRemove(BlockBreakEvent event) {

        Block block = event.getBlock();
        if (block == null)
            return;
        if (isBed(block)) {
            Block head = getHeadOfBed(block);
            if (head == null)
                return;
            removeSpawn(head.getLocation());
        }
    }


    private boolean isBed(Block b) {
        switch (b.getType()) {
            case BLACK_BED:
            case BLUE_BED:
            case BROWN_BED:
            case CYAN_BED:
            case GRAY_BED:
            case GREEN_BED:
            case LIGHT_BLUE_BED:
            case LIGHT_GRAY_BED:
            case LIME_BED:
            case MAGENTA_BED:
            case ORANGE_BED:
            case PINK_BED:
            case PURPLE_BED:
            case RED_BED:
            case WHITE_BED:
            case YELLOW_BED:
                return true;
            default:
                return false;
        }
    }

    private Location transform(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + 0.75, location.getBlockZ());
    }

    private void saveSpawn(Player player, Location location) {
        location = getHeadOfBed(location.getBlock()).getLocation();
        location = transform(location);
        Stat stat = Stat.wrap(player.getUniqueId());
        stat.setSpawn(location);
    }

    public static void fixBug() {
        for (Stat stat : Stat.getStatMap().values()) {
            if(stat.getSpawn() == null)
                continue;
            Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(stat.getSpawn());
            UUID uuid = stat.getPlayerUUID();
            if (plot instanceof PlayerPlot && ((PlayerPlot) plot).isTown() && ((PlayerPlot) plot).hasPermissionToUse(uuid))
                continue;
            stat.setSpawn(null);
        }
    }

        private void removeSpawn (Location location){
            location = transform(location);
            for (Stat stat : Stat.getStatMap().values()) {
                Location loc = stat.getSpawn();
                if (loc == null)
                    continue;

                if (loc.equals(location)) {

                    stat.setSpawn(null);
                    Player player = Bukkit.getPlayer(stat.getPlayerUUID());
                    if (player == null)
                        continue;
                    player.sendMessage(ERROR_COLOR + "Your spawnpoint has been reset because your bed has been broken.");
                }
            }
        }

        private Block getHeadOfBed (Block tryingForBed){
            if (tryingForBed == null)
                return null;
            if (!(tryingForBed.getBlockData() instanceof Bed))
                return null;
            Bed bed = (Bed) tryingForBed.getBlockData();

            if (bed.getPart().equals(Bed.Part.HEAD)) {
                return tryingForBed;
            }

            Location actualHead = null;
            switch (bed.getFacing()) {
                case NORTH:
                    actualHead = tryingForBed.getLocation().add(0, 0, -1);
                    break;
                case EAST:
                    actualHead = tryingForBed.getLocation().add(1, 0, 0);
                    break;
                case SOUTH:
                    actualHead = tryingForBed.getLocation().add(0, 0, 1);
                    break;
                case WEST:
                    actualHead = tryingForBed.getLocation().add(-1, 0, 0);
                    break;
            }
            return actualHead.getBlock();
        }


    }
