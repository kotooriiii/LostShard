package com.github.kotooriiii.listeners;

import com.github.kotooriiii.channels.ShardChatEvent;
import com.github.kotooriiii.skills.listeners.BrawlingListener;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class StunListener implements Listener {
    @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void checkStunCommand(PlayerCommandPreprocessEvent event)
    {
        Player player = event.getPlayer();
        if(BrawlingListener.isStunned(player.getUniqueId()))
        {
            player.sendMessage(BrawlingListener.getStunMessage(player.getUniqueId()));
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void checkStunMessage(ShardChatEvent event)
    {
        Player player = event.getPlayer();
        if(BrawlingListener.isStunned(player.getUniqueId()))
        {
            player.sendMessage(BrawlingListener.getStunMessage(player.getUniqueId()));
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void checkStunMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        //If stunned
        if(BrawlingListener.isStunned(player.getUniqueId()))
        {
            Location fromLoc = event.getFrom();
            Location toLoc = event.getTo();

            //If from height was higher than what it will be.
            if(fromLoc.getY() > toLoc.getY())
            {
                //If X match AND Z match, then we can keep moving down.
                if(fromLoc.getBlockX() == toLoc.getBlockX() && fromLoc.getBlockZ() == toLoc.getBlockZ()) {
                    //Set event to same world, to last , to bottom Y, to last Z.
                    event.setTo(new Location(fromLoc.getWorld(), fromLoc.getX(), toLoc.getY(), fromLoc.getZ(), fromLoc.getYaw(), fromLoc.getPitch()));
                    return;
                }
            }
            player.sendMessage(BrawlingListener.getStunMessage(player.getUniqueId()));
            event.setCancelled(true);
        }
    }
}
