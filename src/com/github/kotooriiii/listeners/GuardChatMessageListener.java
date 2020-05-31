package com.github.kotooriiii.listeners;

import com.github.kotooriiii.channels.events.ShardChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GuardChatMessageListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onGuardChatMessage(ShardChatEvent shardChatEvent)
    {

        Player player = shardChatEvent.getPlayer();
        String message = shardChatEvent.getMessage();

        if (message.equalsIgnoreCase("guards") || message.equalsIgnoreCase("guard")) {
            player.performCommand("guards");
            shardChatEvent.setCancelled(true);

        }

    }
}
