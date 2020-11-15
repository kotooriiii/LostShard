package com.github.kotooriiii.commands;

import com.github.kotooriiii.hostility.HostilityPlatform;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class HelpCommandListener implements Listener {
    @EventHandler
    public void onHelpCMD(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.length() <= 1)
            return;

        if (message.substring(1).startsWith("help")) {
            player.performCommand("doc");
            player.performCommand("yt");
            player.performCommand("wiki");
            player.performCommand("discord");

            event.setCancelled(true);

        }
    }
}
