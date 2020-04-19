package com.github.kotooriiii.listeners;

import com.github.kotooriiii.hostility.HostilityPlatform;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static com.github.kotooriiii.data.Maps.platforms;

public class HostilityNamePreprocessListener implements Listener {
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onHostilityCMD(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        if(message.length() <= 1)
            return;

        for (HostilityPlatform platform : platforms) {
            if (platform.getName().equalsIgnoreCase(message.substring(1))) {
                player.performCommand("host " + platform.getName());
                event.setCancelled(true);
            }
        }
    }
}
