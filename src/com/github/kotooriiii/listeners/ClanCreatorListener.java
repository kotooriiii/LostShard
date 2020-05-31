package com.github.kotooriiii.listeners;

import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.clans.Clan;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class ClanCreatorListener implements Listener {

    @EventHandler
    public void onChat(ShardChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();


        if (clanTagCreators.containsKey(uuid)) {
            e.setCancelled(true);

            String tag = e.getMessage();
            Clan clan = clanTagCreators.get(uuid);
            if (clan == null || !clan.hasEditingPermission(uuid)) {
                player.sendMessage(ERROR_COLOR + "You have no permission to edit your clan.");
                clanTagCreators.remove(uuid);
                return;
            }

            //longer or shorter than 3.
            if (tag.length() != 3) {
                player.sendMessage(ERROR_COLOR + "Your clan tag can not be longer or shorter than 3 characters. Please try again.");
                return;

            }

            //contains ! $ &^
            if (!StringUtils.isAlphanumeric(tag)) {
                player.sendMessage(ERROR_COLOR + "Your clan tag can not have special characters. Please try again.");
                return;
            }

            for (Clan iclan : clans) {
                if (iclan.getTag().toLowerCase().equals(tag.toLowerCase())) {
                    player.sendMessage(ERROR_COLOR + "There is already a clan with that tag.");
                    return;

                }
            }


            clan.broadcast(STANDARD_COLOR + "Clan tag has been set to \"" + tag + STANDARD_COLOR + "\".");

            // player.sendMessage("Now choose a color! like &6 etc");

            clan.forceTag(tag);
            clan.saveFile();
            clanTagCreators.remove(uuid);
            //+   clanColorCreators.put(uuid, clan);
        } else if (clanColorCreators.containsKey(uuid)) {
            e.setCancelled(true);
            String message = e.getMessage();

            Clan clan = clanColorCreators.get(uuid);
            if (clan == null || !clan.hasEditingPermission(uuid)) {
                player.sendMessage(ERROR_COLOR + "You have no permission to edit your clan.");
                clanColorCreators.remove(uuid);
                return;
            }
            //Bans bolds, underlines, etc. Also only keeps ONE COLOR
            if (!message.matches("&([0-9]|[A-F]|[a-f]){1}")) {
                player.sendMessage(STANDARD_COLOR + "Choose a color to represent your clan. For example, &7.");
                return;
            }


            ChatColor color = ChatColor.getByChar(message.replace('&', ChatColor.COLOR_CHAR));
            clan.setColor(color);
            clan.saveFile();
            clanColorCreators.remove(uuid);
            player.sendMessage(STANDARD_COLOR + "The customization of your clan is complete!");


        }

    }
}
