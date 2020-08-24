package com.github.kotooriiii.channels.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.IgnorePlayer;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class IgnoreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player))
            return false;
        if (!cmd.getName().equalsIgnoreCase("ignore"))
            return false;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(STANDARD_COLOR + "To ignore a player: " + ChatColor.YELLOW + "/ignore (username)" + STANDARD_COLOR + ".");
            return false;
        }

        String username = args[0];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
        if(!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline())
        {
            player.sendMessage(ERROR_COLOR + "This player does not exist in this server.");
            return false;
        }

        if(offlinePlayer.getUniqueId().equals(player.getUniqueId()))
        {
            player.sendMessage(ERROR_COLOR + "You can't ignore yourself... embrace yourself.");
            return false;
        }

        IgnorePlayer ignorePlayer = LostShardPlugin.getIgnoreManager().wrap(player.getUniqueId());
        StatusPlayer statusPlayer = StatusPlayer.wrap(offlinePlayer.getUniqueId());
        ChatColor color = statusPlayer.getStatus().getChatColor();
        if(ignorePlayer.isIgnoring(offlinePlayer.getUniqueId()))
        {
            ignorePlayer.unignore(offlinePlayer.getUniqueId());
            player.sendMessage(STANDARD_COLOR + "You have removed " + color + offlinePlayer.getName() + STANDARD_COLOR + " from your ignore list.");
        } else {
            ignorePlayer.ignore(offlinePlayer.getUniqueId());
            player.sendMessage(STANDARD_COLOR + "You have added " + color + offlinePlayer.getName() + STANDARD_COLOR + " to your ignore list.");
        }

        return true;
    }
}
