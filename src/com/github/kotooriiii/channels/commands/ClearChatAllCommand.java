package com.github.kotooriiii.channels.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class ClearChatAllCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))
            return false;
        if(!command.getName().equalsIgnoreCase("clearchatall"))
            return false;
        if(!commandSender.hasPermission(STAFF_PERMISSION))
            return false;
        /*
        Is a player
        Has staff permission
        Command is /clearchat
         */


        int numOfMessages = 101;

        for(Player player : Bukkit.getOnlinePlayers())
        {
            for(int i = 0; i < numOfMessages; i++)
            {
                player.sendMessage(" ");

            }
        }
        return true;
    }

}
