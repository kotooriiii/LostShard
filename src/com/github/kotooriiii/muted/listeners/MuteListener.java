package com.github.kotooriiii.muted.listeners;

import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.muted.MutedPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class MuteListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMute(ShardChatEvent event) {
        if (MutedPlayer.getMutedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            if(event.getPlayer().hasPermission(STAFF_PERMISSION))
            {
                MutedPlayer.getMutedPlayers().get(event.getPlayer().getUniqueId()).remove();
                return;
            }
            event.getPlayer().sendMessage(ERROR_COLOR +  "You are currently muted.");
            event.setCancelled(true);
            return;
        }
    }
}
