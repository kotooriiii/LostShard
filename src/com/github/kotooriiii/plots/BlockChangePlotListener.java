package com.github.kotooriiii.plots;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class BlockChangePlotListener implements Listener {
    @EventHandler
    public void onBlockChangePlot(EntityChangeBlockEvent entityChangeBlockEvent) {
        final Block block = entityChangeBlockEvent.getBlock();
        final Location location = block.getLocation();
        final Entity en = entityChangeBlockEvent.getEntity();

        //Iterate through all plots
        for (Plot plot : Plot.getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {
                //Check entity
                //If entity is not a player then cancel it
                if (!(en instanceof Player)) {
                    entityChangeBlockEvent.setCancelled(true);
                    return;
                }
                final Player playerInteracting = (Player) en;
                final UUID playerUUID = playerInteracting.getUniqueId();

                if(playerInteracting.hasPermission(STAFF_PERMISSION))
                    return;

                //If don't have permissions
                if (!(plot.isJointOwner(playerUUID) || plot.isOwner(playerUUID))) {
                    entityChangeBlockEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    @EventHandler
    public void onBlockChangePlot(BlockBreakEvent blockBreakEvent) {
        final Block block = blockBreakEvent.getBlock();
        final Location location = block.getLocation();
        //Check entity
        final Player playerBlockBreak = blockBreakEvent.getPlayer();
        //If entity is not a player then cancel it
        final UUID playerUUID = playerBlockBreak.getUniqueId();

        if(playerBlockBreak.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : Plot.getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {


                //If don't have permissions
                if (!(plot.isJointOwner(playerUUID) || plot.isOwner(playerUUID))) {
                    blockBreakEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    @EventHandler
    public void onBlockPlaceChangePlot(BlockPlaceEvent blockPlaceEvent) {
        final Block block = blockPlaceEvent.getBlock();
        final Location location = block.getLocation();
        //Check entity
        final Player playerBlockBreak = blockPlaceEvent.getPlayer();
        //If entity is not a player then cancel it
        final UUID playerUUID = playerBlockBreak.getUniqueId();

        if(playerBlockBreak.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : Plot.getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {


                //If don't have permissions
                if (!(plot.isJointOwner(playerUUID) || plot.isOwner(playerUUID))) {
                    blockPlaceEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }
}
