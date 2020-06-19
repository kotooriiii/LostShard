package com.github.kotooriiii.hostility.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.hostility.Zone;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.github.kotooriiii.data.Maps.*;

import java.util.List;
import java.util.UUID;

public class HostilityCreateListener implements Listener {
    @EventHandler
    public void onClickWithItem(PlayerInteractEvent playerInteractEvent) {

        Player player = playerInteractEvent.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!hostilityPlatformCreator.containsKey(playerUUID))
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
            case "ID:AREA_AXE":
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
                    int blockPos1Y = Integer.parseInt(coordsPos1[1]);
                    int blockPos1Z = Integer.parseInt(coordsPos1[2]);

                    int indexOfOpen2 = possiblePos2.indexOf(':') + 1;
                    int indexOfClose2 = possiblePos2.indexOf(')');
                    String blockPos2 = possiblePos2.substring(indexOfOpen2, indexOfClose2);
                    String[] coordsPos2 = blockPos2.split(",");
                    int blockPos2X = Integer.parseInt(coordsPos2[0]);
                    int blockPos2Y = Integer.parseInt(coordsPos2[1]);
                    int blockPos2Z = Integer.parseInt(coordsPos2[2]);

                    Location loc1 = new Location(LostShardPlugin.plugin.getServer().getWorld("world"), blockPos1X, blockPos1Y, blockPos1Z);
                    Location loc2 = new Location(LostShardPlugin.plugin.getServer().getWorld("world"), blockPos2X, blockPos2Y, blockPos2Z);

                    //set area
                    Zone zone = new Zone(loc1, loc2);

                    HostilityPlatform platform = (HostilityPlatform) hostilityPlatformCreator.get(playerUUID);
                    platform.addZone(zone);
                    //reset
                    possiblePos1 = possiblePos1.substring(0, possiblePos1.indexOf('('));
                    possiblePos2 = possiblePos2.substring(0, possiblePos2.indexOf('('));
                    player.sendMessage(STANDARD_COLOR + "You have added an area zone.");

                }

                loreList.set(1, possiblePos1);
                loreList.set(2, possiblePos2);
                itemMeta.setLore(loreList);
                item.setItemMeta(itemMeta);
                break;
            case "ID:SINGLE_SWORD":
                if (block == null || block.getType().equals(Material.AIR))
                    return;

                //if you left click
                if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                    Location loc1 = block.getLocation();
                    Location loc2 = new Location(loc1.getWorld(), loc1.getBlockX(), loc1.getY() + 2, loc1.getBlockZ());
                    //set area
                    Zone zone = new Zone(loc1, loc2);
                    HostilityPlatform platform = hostilityPlatformCreator.get(playerUUID);
                    platform.addZone(zone);
                    player.sendMessage(STANDARD_COLOR + "You have added a zone.");

                    playerInteractEvent.setCancelled(true);

                }
                break;
            case "ID:FINALIZE_FLOWER":
                if (!action.equals(Action.PHYSICAL)) {
                    HostilityPlatform platform = hostilityPlatformCreator.get(playerUUID);
                    platform.runCountdown();
                    platforms.add(platform);
                    FileManager.write(platform);
                    player.getInventory().clear();
                    player.sendMessage(STANDARD_COLOR + "You have saved " + platform.getName() + ".");
                    hostilityPlatformCreator.remove(player.getUniqueId());
                    playerInteractEvent.setCancelled(true);

                    //removed in block event

                }
                break;
            case "ID:UNDO_RED":
                if (!action.equals(Action.PHYSICAL)) {
                    HostilityPlatform platform = hostilityPlatformCreator.get(playerUUID);
                    if (platform.undo()) {
                        player.sendMessage(STANDARD_COLOR + "You have just removed the most recently added zone.");
                    } else {
                        player.sendMessage(STANDARD_COLOR + "There are no zones to undo.");

                    }

                    playerInteractEvent.setCancelled(true);

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
            case "ID:FINALIZE_FLOWER":
                blockPlaceEvent.setCancelled(true);
        }
    }
}
