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
     *
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
            return;

            //  if (block.getState() instanceof Container)
        }

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {


            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //if not a player plot AND its a redstone thingy, then return other wise cancel
                if (block.getType().getKey().getKey().toUpperCase().endsWith("BUTTON") || block.getBlockData() instanceof Powerable || block.getType().getKey().getKey().toUpperCase().endsWith("_BUTTON") || block.getType().getKey().getKey().toUpperCase().endsWith("_PRESSURE_PLATE") || block.getType() == Material.LEVER) {
                    return;
                } else {
                    entityInteractEvent.setCancelled(true);
                }

//
//                //ALLOWED

                break;
            }
        }
    }

    /**
     * Stops the RIGHT CLICK interaction on enemy plots with the exception of wooden stuff.
     *
     * @param playerInteractEvent
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        final Block block = playerInteractEvent.getClickedBlock();
        if (block == null)
            return;
        final Location location = block.getLocation();
        final Player playerInteracting = playerInteractEvent.getPlayer();
        final UUID playerUUID = playerInteracting.getUniqueId();

        //Staff override
        if (playerInteracting.hasPermission(STAFF_PERMISSION))
            return;

        //These "wooden" items CAN ALWAYS be used.
        if ((block.getType() == Material.BIRCH_DOOR || block.getType() == Material.ACACIA_DOOR || block.getType() == Material.DARK_OAK_DOOR || block.getType() == Material.JUNGLE_DOOR || block.getType() == Material.SPRUCE_DOOR || block.getType() == Material.OAK_DOOR ||
                block.getType() == Material.BIRCH_BUTTON || block.getType() == Material.ACACIA_BUTTON || block.getType() == Material.DARK_OAK_BUTTON || block.getType() == Material.JUNGLE_BUTTON || block.getType() == Material.SPRUCE_BUTTON || block.getType() == Material.OAK_BUTTON ||
                block.getType() == Material.BIRCH_TRAPDOOR || block.getType() == Material.ACACIA_TRAPDOOR || block.getType() == Material.DARK_OAK_TRAPDOOR || block.getType() == Material.JUNGLE_TRAPDOOR || block.getType() == Material.SPRUCE_TRAPDOOR || block.getType() == Material.OAK_TRAPDOOR ||
                block.getType() == Material.BIRCH_PRESSURE_PLATE || block.getType() == Material.ACACIA_PRESSURE_PLATE || block.getType() == Material.DARK_OAK_PRESSURE_PLATE || block.getType() == Material.JUNGLE_PRESSURE_PLATE || block.getType() == Material.SPRUCE_PRESSURE_PLATE || block.getType() == Material.OAK_PRESSURE_PLATE
                && (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR))
        )
            return;

        //Buggy so remove stairs
        if (block.getType().getKey().getKey().toLowerCase().endsWith("stairs") && (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR))
            return;
        //If it it isn't interactable, some glitchy stuff happens
        if (!block.getType().isInteractable() && (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR))
            return;

        //If it's a container, ANYONE can use it
        if (block.getState() instanceof Container)
            return;

        //Not a right click event, stop
        if (!(playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;


        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {


            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //A staff plot can use powerables, buttons, pressure plate
                if (!plot.getType().equals(PlotType.PLAYER)) {
                    if (block.getBlockData() instanceof Powerable || block.getType().getKey().getKey().toUpperCase().endsWith("_BUTTON") || block.getType().getKey().getKey().toUpperCase().endsWith("_PRESSURE_PLATE") || block.getType() == Material.LEVER)
                        return;
                    playerInteractEvent.setCancelled(true);
                    return;
                }


                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!playerPlot.hasPermissionToUse(playerUUID)) {
                    playerInteractEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }
}