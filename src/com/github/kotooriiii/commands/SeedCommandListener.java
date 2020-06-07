package com.github.kotooriiii.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class SeedCommandListener implements Listener {
    @EventHandler
    public void onHelpCMD(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.length() <= 1)
            return;

        if (message.substring(1).startsWith("seed")) {
player.sendMessage(ERROR_COLOR + "The seed is not open to the community.");
            event.setCancelled(true);

        }
    }
}