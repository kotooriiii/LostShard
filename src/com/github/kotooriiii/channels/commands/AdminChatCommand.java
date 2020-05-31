package com.github.kotooriiii.channels.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.ChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class AdminChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if(!sender.hasPermission(STAFF_PERMISSION))
            return false;

        if(!cmd.getName().equalsIgnoreCase("adminchat"))
            return false;

        /*
        Has staff permission
        Is /adminchat
         */

        //Admin chat mode
        ChannelManager channelManager = LostShardPlugin.getChannelManager();

        boolean isCurrentlyAdminChat = channelManager.isAdminChat();

        if(isCurrentlyAdminChat)
        {
            for(Player player : Bukkit.getOnlinePlayers())
                player.sendMessage(STANDARD_COLOR + "You have switched to admin chat.");
        } else {
            for(Player player : Bukkit.getOnlinePlayers())
                player.sendMessage(STANDARD_COLOR + "You have switched to " + LostShardPlugin.getChannelManager().getChannel(player).getName().toLowerCase() + " chat.");
        }

        channelManager.setAdminChat(!isCurrentlyAdminChat);
        return true;
    }
}
