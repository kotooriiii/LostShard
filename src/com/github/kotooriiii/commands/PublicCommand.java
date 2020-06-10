package com.github.kotooriiii.commands;

import com.github.kotooriiii.stats.Stat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PublicCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(!(commandSender instanceof Player))
            return false;

        if(!command.getName().equalsIgnoreCase("public"))
            return false;

        //is private cmd and is player

        Player player = (Player) commandSender;
        Stat stat = Stat.wrap(player.getUniqueId());

        if(stat.isPrivate())
        {
            player.sendMessage(ChatColor.GOLD + "You have set your clan tp status to public.");
            stat.setPrivate(!stat.isPrivate());
        } else {
            player.sendMessage(ERROR_COLOR + "You have already set your clan tp status to public. Did you mean to set yourself to private? /private");
        }
        return true;

    }
}