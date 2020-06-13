package com.github.kotooriiii.listeners;

import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class NotValidReachBlockListener implements Listener {
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Block eventBlock = event.getClickedBlock();
        if(eventBlock == null)
            return;
        Player player = event.getPlayer();
        Block calcBlock = player.getTargetBlockExact(5, FluidCollisionMode.NEVER);
        if(calcBlock == null)
            return;

        if(calcBlock.equals(eventBlock))
            return;

        //player.sendMessage(ERROR_COLOR + "You can't reach that.");
        event.setCancelled(true);

    }
}
