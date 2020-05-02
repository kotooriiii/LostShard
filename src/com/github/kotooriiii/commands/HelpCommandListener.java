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

        if(message.length() <= 1)
            return;

        if(message.substring(1).startsWith("help"))
        {
            TextComponent tc = new TextComponent("-Help-\n" +
                    "For information regarding the server’s features, check out\nour wiki/info page at:\n");
            tc.setColor(net.md_5.bungee.api.ChatColor.GOLD);

            TextComponent component = new TextComponent("https://docs.google.com/document/d/1UfFwn_xJrgPkjKC9Bs7OAFAG22IDhPg4wmk7cZtsv9c/edit?usp=sharing");
            component.setColor(ChatColor.GOLD);
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to help site.").color(ChatColor.GOLD).create()));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://docs.google.com/document/d/1UfFwn_xJrgPkjKC9Bs7OAFAG22IDhPg4wmk7cZtsv9c/edit?usp=sharing"));
            tc.addExtra(component.duplicate());

            player.spigot().sendMessage(tc.duplicate());
            event.setCancelled(true);

        }
    }
}
