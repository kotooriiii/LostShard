package com.github.kotooriiii.channels.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class ClearChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))
            return false;
        if(!command.getName().equalsIgnoreCase("clearchat"))
            return false;
        /*
        Is a player
        Command is /clearchat
         */

        int numOfMessages = 101;
        for(int i = 0; i < numOfMessages; i++)
        {
            commandSender.sendMessage(" ");
        }
        return true;
    }
}
