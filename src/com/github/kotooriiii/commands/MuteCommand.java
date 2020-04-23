package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.muted.MutedPlayer;
import com.github.kotooriiii.util.HelperMethods;
import com.mojang.datafixers.kinds.IdF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class MuteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("mute"))
            return false;
        final Player playerSender = (Player) commandSender;

        if(!playerSender.hasPermission(STAFF_PERMISSION))
        {
            playerSender.sendMessage(ERROR_COLOR + "You must be staff to access this command.");
            return false;
        }

        if (args.length != 1) {
            playerSender.sendMessage(ERROR_COLOR + "The proper usage of the command is: " + COMMAND_COLOR + "/mute (username)" + ERROR_COLOR + ".");
            return false;
        }

        String name = args[0];

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (!offlinePlayer.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player you are looking for does not exist.");
            return false;
        }

        if(offlinePlayer.isOnline()) {
            if(offlinePlayer.getPlayer().hasPermission(STAFF_PERMISSION))
            {
                playerSender.sendMessage(ERROR_COLOR + "The player cannot be muted.");
                return false;

            }
        }

        boolean isMuting = !MutedPlayer.getMutedPlayers().containsKey(offlinePlayer.getUniqueId());

        if(isMuting)
        {
            playerSender.sendMessage(ChatColor.RED + "You have muted " + PLAYER_COLOR + offlinePlayer.getName() + ChatColor.RED + ".");
            if(offlinePlayer.isOnline())
                offlinePlayer.getPlayer().sendMessage(ERROR_COLOR + "You have been muted.");
            MutedPlayer mutedPlayer = new MutedPlayer(offlinePlayer.getUniqueId());
            mutedPlayer.add();

        }
        else
        {
            playerSender.sendMessage(ChatColor.RED + "You have unmuted " + PLAYER_COLOR + offlinePlayer.getName() + ChatColor.RED + ".");
            if(offlinePlayer.isOnline())
                offlinePlayer.getPlayer().sendMessage(ERROR_COLOR + "You have been unmuted.");
            MutedPlayer.getMutedPlayers().get(offlinePlayer.getUniqueId()).remove();
        }
        return true;
    }

}
