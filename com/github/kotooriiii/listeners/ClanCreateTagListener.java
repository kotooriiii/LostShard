package com.github.kotooriiii.listeners;

import com.github.kotooriiii.clans.Clan;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.clanColorCreators;
import static com.github.kotooriiii.data.Maps.clanTagCreators;

public class ClanCreateTagListener implements Listener {

        @EventHandler
        public void onChat(AsyncPlayerChatEvent e)
        {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            if(clanTagCreators.containsKey(uuid))
            {
                String tag = e.getMessage();
                //longer or shorter than 3.
                if(tag.length() != 3)
                {
                    player.sendMessage(ChatColor.RED + "Your clan tag can not be longer or shorter than 3 characters. Please try again.");
                    return;

                }

                //contains ! $ &^
                if(!StringUtils.isAlphanumeric(tag))
                {
                    player.sendMessage(ChatColor.RED + "Your clan tag can not have special characters. Please try again.");
                    return;
                }

                player.sendMessage(ChatColor.GREEN + "Clan tag has been set to “" + ChatColor.YELLOW + tag + ChatColor.GREEN + "”.");
                player.sendMessage("Now choose a color! like &6 etc");
                Clan clan = clanTagCreators.get(uuid);
                clan.setTag(tag);
                clanTagCreators.remove(uuid);
                clanColorCreators.put(uuid, clan);



            } else if(clanColorCreators.containsKey(uuid))
            {
                String message = e.getMessage();

                //Bans bolds, underlines, etc. Also only keeps ONE COLOR
                if(!message.matches("&([0-9]|[A-F]|[a-f]){1}"))
                {
                    player.sendMessage("You need to keep it like &6 or &3. length of two and code.");
                    return;
                }

                ChatColor color = ChatColor.valueOf("d");
                Clan clan = clanColorCreators.get(uuid);
                clan.setColor(color);
                clanColorCreators.remove(uuid);
                player.sendMessage("customizing clan done");


            }

        }
}
