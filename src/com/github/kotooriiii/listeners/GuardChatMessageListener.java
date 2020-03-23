package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.ShardChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

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
