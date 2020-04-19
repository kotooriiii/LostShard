package com.github.kotooriiii.plots;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.npc.ShardNMS;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class PlotStaffCreateListener implements Listener {
    @EventHandler
    public void onClickWithItem(PlayerInteractEvent playerInteractEvent) {

        Player player = playerInteractEvent.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!staffPlotCreator.containsKey(playerUUID))
            return;

        ItemStack item = playerInteractEvent.getItem();
        if (item == null)
            return;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null)
            return;
        List<String> loreList = itemMeta.getLore();
        if (loreList == null || loreList.size() == 0)
            return;
        String id = loreList.get(loreList.size() - 1);

        Action action = playerInteractEvent.getAction();
        Block block = playerInteractEvent.getClickedBlock();

        switch (id) {
            case "ID:PLOT_AREA_AXE":
                //The selected block isn't air and it is a valid block
                if (block == null || block.getType().equals(Material.AIR))
                    return;

                //Get left click lore
                String possiblePos1 = loreList.get(1);
                //Get right click lore
                String possiblePos2 = loreList.get(2);

                //if you left click
                if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                    //if the left click lore has a position found.
                    if (possiblePos1.indexOf('(') != -1) {
                        //Since we have just left clicked a block we are trying to update so remove previous entry.
                        possiblePos1 = possiblePos1.substring(0, possiblePos1.indexOf('('));
                    }

                    //The string now contains the new position.
                    player.sendMessage(STANDARD_COLOR + "You have selected Pos1(" + (int) Math.floor(block.getX()) + "," + (int) Math.floor(block.getY()) + "," + (int) Math.floor(block.getZ()) + ").");
                    possiblePos1 = possiblePos1 + " (Pos1:" + (int) Math.floor(block.getX()) + "," + (int) Math.floor(block.getY()) + "," + (int) Math.floor(block.getZ()) + ")";

                    playerInteractEvent.setCancelled(true);

                } else if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                    //if the right click lore has a position found.
                    if (possiblePos2.indexOf('(') != -1) {
                        //Since we have just right clicked a block we are trying to update so remove previous entry.
                        possiblePos2 = possiblePos2.substring(0, possiblePos2.indexOf('('));
                    }

                    //The string now contains the new position.
                    player.sendMessage(STANDARD_COLOR + "You have selected Pos2(" + (int) Math.floor(block.getX()) + "," + (int) Math.floor(block.getY()) + "," + (int) Math.floor(block.getZ()) + ").");
                    possiblePos2 = possiblePos2 + " (Pos2:" + (int) Math.floor(block.getX()) + "," + (int) Math.floor(block.getY()) + "," + (int) Math.floor(block.getZ()) + ")";
                }

                //Both have a set position value
                if (possiblePos1.indexOf('(') != -1 && possiblePos2.indexOf('(') != -1) {
                    int indexOfOpen = possiblePos1.indexOf(':') + 1;
                    int indexOfClose = possiblePos1.indexOf(')');
                    String blockPos1 = possiblePos1.substring(indexOfOpen, indexOfClose);
                    String[] coordsPos1 = blockPos1.split(",");
                    int blockPos1X = Integer.parseInt(coordsPos1[0]);
                    int blockPos1Y = 0; //Integer.parseInt(coordsPos1[1]);
                    int blockPos1Z = Integer.parseInt(coordsPos1[2]);

                    int indexOfOpen2 = possiblePos2.indexOf(':') + 1;
                    int indexOfClose2 = possiblePos2.indexOf(')');
                    String blockPos2 = possiblePos2.substring(indexOfOpen2, indexOfClose2);
                    String[] coordsPos2 = blockPos2.split(",");
                    int blockPos2X = Integer.parseInt(coordsPos2[0]);
                    int blockPos2Y = 256; //Integer.parseInt(coordsPos2[1]);
                    int blockPos2Z = Integer.parseInt(coordsPos2[2]);

                    Location loc1 = new Location(LostShardPlugin.plugin.getServer().getWorld("world"), blockPos1X, blockPos1Y, blockPos1Z);
                    Location loc2 = new Location(LostShardPlugin.plugin.getServer().getWorld("world"), blockPos2X, blockPos2Y, blockPos2Z);

                    //set area
                    Zone zone = new Zone(loc1, loc2);

                    Object[] properties = staffPlotCreator.get(playerUUID);

                    if (properties[0] == null)
                        player.sendMessage(STANDARD_COLOR + "You have added an area zone.");
                    else
                        player.sendMessage(STANDARD_COLOR + "You have replaced the last area zone.");

                    properties[0] = zone;
                    staffPlotCreator.put(playerUUID, properties);
                    //reset
                    possiblePos1 = possiblePos1.substring(0, possiblePos1.indexOf('('));
                    possiblePos2 = possiblePos2.substring(0, possiblePos2.indexOf('('));

                }

                loreList.set(1, possiblePos1);
                loreList.set(2, possiblePos2);
                itemMeta.setLore(loreList);
                item.setItemMeta(itemMeta);
                break;
            case "ID:PLOT_FINALIZE_FLOWER":
                if (!action.equals(Action.PHYSICAL)) {

                    Object[] properties = staffPlotCreator.get(playerUUID);
                    Zone zoneFinish = (Zone) properties[0];
                    String name = (String) properties[1];

                    boolean isFound = false;
                    Plot plotFound = null;

                    for (Iterator<Plot> iterator = Plot.getAllPlots().iterator(); iterator.hasNext(); ) {
                        Plot next = iterator.next();
                        if (next.getName().equalsIgnoreCase(name)) {
                            player.sendMessage(ERROR_COLOR + "The last plot with this name has been cleared. What this means is that the last spawn was also removed.");
                            plotFound = next;
                            isFound = true;
                        }
                    }

                    if (isFound)
                        plotFound.disband();


                    if (name.equalsIgnoreCase("arena"))
                    {
                        Plot plot = new ArenaPlot(zoneFinish, name); //auto does it

                    }
                    else {
                        Plot plot = new Plot(zoneFinish, name); //auto does it
                    }

                    player.getInventory().clear();
                    player.sendMessage(STANDARD_COLOR + "You have saved the staff plot, \"" + name + "\".");
                    staffPlotCreator.remove(player.getUniqueId());
                    playerInteractEvent.setCancelled(true);

                    //removed in block event
                }
                break;
            default:
                return;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {

        ItemStack item = blockPlaceEvent.getItemInHand();
        if (item == null)
            return;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null)
            return;
        List<String> loreList = itemMeta.getLore();
        if (loreList == null || loreList.size() == 0)
            return;
        String id = loreList.get(loreList.size() - 1);


        switch (id) {
            case "ID:PLOT_FINALIZE_FLOWER":
                blockPlaceEvent.setCancelled(true);
        }
    }

}
