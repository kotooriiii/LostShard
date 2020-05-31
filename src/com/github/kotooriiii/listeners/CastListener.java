package com.github.kotooriiii.listeners;

import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.commands.CastCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CastListener implements Listener {
    @EventHandler
    public void onCast(ShardChatEvent event) {
        if (CastCommand.markCommand.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().performCommand("cast mark " + event.getMessage());
            event.setCancelled(true);
        } else if (CastCommand.recallCommand.containsKey(event.getPlayer().getUniqueId())){
            event.getPlayer().performCommand("cast recall " + event.getMessage());
            event.setCancelled(true);
        } else if(CastCommand.clantpCommand.contains(event.getPlayer().getUniqueId()))
        {
            event.getPlayer().performCommand("cast clantp " + event.getMessage());
            event.setCancelled(true);
        }
    }
}
