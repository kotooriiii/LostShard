package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import sun.text.resources.cldr.ka.FormatData_ka;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class EntityInteractPlotListener implements Listener {

    /**
     * Called when an Entity attemp
     * @param entityInteractEvent
     */
    @EventHandler
    public void onEntityInteractPlot(EntityInteractEvent entityInteractEvent) {
        final Block block = entityInteractEvent.getBlock();
        final Location location = block.getLocation();
        if (block == null)
            return;

        //Check entity
        Entity en = entityInteractEvent.getEntity();

        if (en instanceof Player) {
            if (block.getState() instanceof Container)
                return;
        }

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {


            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                if (!plot.getType().equals(PlotType.PLAYER)) {
                    if (block.getType().getKey().getKey().toUpperCase().endsWith("BUTTON") || block.getBlockData() instanceof Powerable || block.getType().getKey().getKey().toUpperCase().endsWith("_BUTTON") || block.getType().getKey().getKey().toUpperCase().endsWith("_PRESSURE_PLATE") || block.getType() == Material.LEVER)
                        return;
                    entityInteractEvent.setCancelled(true);
                    return;
                }


                //If entity is not a player then cancel it
                if (!(en instanceof Player)) {
                    entityInteractEvent.setCancelled(true);
                    return;
                }

//                final Player playerInteracting = (Player) en;
//                final UUID playerUUID = playerInteracting.getUniqueId();
//
//                if (playerInteracting.hasPermission(STAFF_PERMISSION))
//                    return;
//                //If don't have permissions
//
//                PlayerPlot playerPlot = (PlayerPlot) plot;
//                if (!(playerPlot.isFriend(playerUUID) || playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
//                    entityInteractEvent.setCancelled(true);
//                    return;
//                }
//
//                //ALLOWED

                break;
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        final Block block = playerInteractEvent.getClickedBlock();
        if (block == null)
            return;
        final Location location = block.getLocation();
        final Player playerInteracting = playerInteractEvent.getPlayer();
        final UUID playerUUID = playerInteracting.getUniqueId();

        if (playerInteracting.hasPermission(STAFF_PERMISSION))
            return;
        if (block.getType().getKey().getKey().toLowerCase().endsWith("stairs") && (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR))
            return;
        if (!block.getType().isInteractable() && (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR))
            return;

        if (block.getState() instanceof Container)
            return;


        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {


            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                if (!plot.getType().equals(PlotType.PLAYER)) {
                    if (block.getType().getKey().getKey().toUpperCase().endsWith("BUTTON") || block.getBlockData() instanceof Powerable || block.getType().getKey().getKey().toUpperCase().endsWith("_BUTTON") || block.getType().getKey().getKey().toUpperCase().endsWith("_PRESSURE_PLATE") || block.getType() == Material.LEVER)
                        return;
                    playerInteractEvent.setCancelled(true);
                    return;
                }


                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isFriend(playerUUID) || playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    playerInteractEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }
}