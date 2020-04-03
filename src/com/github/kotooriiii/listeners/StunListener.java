package com.github.kotooriiii.listeners;

import com.github.kotooriiii.channels.ShardChatEvent;
import com.github.kotooriiii.skills.listeners.BrawlingListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class StunListener implements Listener {
    @EventHandler
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
        if(BrawlingListener.isStunned(player.getUniqueId()))
        {
            player.sendMessage(BrawlingListener.getStunMessage(player.getUniqueId()));
            event.setCancelled(true);
        }
    }
}
