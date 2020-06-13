package com.github.kotooriiii.listeners;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.swing.tree.ExpandVetoException;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class NotValidMoveBlockListener implements Listener {
    @EventHandler
    public void onClick(PlayerMoveEvent event) {
//        int fX = event.getFrom().getBlockX();
//        int fZ = event.getFrom().getBlockZ();
//
//        int tX = event.getTo().getBlockX();
//        int tZ = event.getTo().getBlockZ();
//
//        Block feet = event.getPlayer().getLocation().getBlock();
//        Block head = feet.getRelative(BlockFace.UP);
//
//        if (!head.getType().isSolid()) {
//            return;
//        }
//
//        //not in the same block left right
//        if (fX == tX && fZ == tZ)
//            return;
//
//        Bukkit.broadcastMessage("unsolid");
//       // event.setTo(new Location(event.getFrom().getWorld(), event.getFrom().getBlockX() + 0.5, event.getFrom().getBlockY(), event.getFrom().getBlockZ() + 0.5));
//        event.setCancelled(true);
    }
}
