package com.github.kotooriiii.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

public class GuardChatMessageListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onGuardChatMessage(PlayerChatEvent asyncPlayerChatEvent)
    {
        Player player = asyncPlayerChatEvent.getPlayer();
        String message = asyncPlayerChatEvent.getMessage();

        if(message.equalsIgnoreCase("guards") || message.equalsIgnoreCase("guard"))
        {
            player.performCommand("guards");
            asyncPlayerChatEvent.setCancelled(true);
        }
    }
}
