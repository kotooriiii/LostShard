package com.github.kotooriiii.plots;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class EntityInteractPlotListener implements Listener {
    @EventHandler
    public void onEntityInteractPlot(EntityInteractEvent entityInteractEvent) {
        final Block block = entityInteractEvent.getBlock();
        final Location location = block.getLocation();
        if(block == null)
            return;

        //Check entity
        Entity en = entityInteractEvent.getEntity();

        //Iterate through all plots
        for (Plot plot : Plot.getPlayerPlots().values()) {
            //If the block being interacted is in the location of a plot
            if(plot.contains(location))
            {

                //If entity is not a player then cancel it
                if(!(en instanceof Player))
                {
                    entityInteractEvent.setCancelled(true);
                    return;
                }

                final Player playerInteracting = (Player) en;
                final UUID playerUUID = playerInteracting.getUniqueId();

                if(playerInteracting.hasPermission(STAFF_PERMISSION))
                    return;
                //If don't have permissions
                if(!(plot.isFriend(playerUUID) || plot.isJointOwner(playerUUID) || plot.isOwner(playerUUID)))
                {
                    entityInteractEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        final Block block = playerInteractEvent.getClickedBlock();
        if(block == null)
            return;
        final Location location = block.getLocation();
        final Player playerInteracting = playerInteractEvent.getPlayer();
        final UUID playerUUID = playerInteracting.getUniqueId();

        if(playerInteracting.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : Plot.getPlayerPlots().values()) {
            //If the block being interacted is in the location of a plot
            if(plot.contains(location))
            {

                //If don't have permissions
                if(!(plot.isFriend(playerUUID) || plot.isJointOwner(playerUUID) || plot.isOwner(playerUUID)))
                {
                    playerInteractEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }
}