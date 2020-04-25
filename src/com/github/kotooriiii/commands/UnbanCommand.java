package com.github.kotooriiii.commands;

import com.github.kotooriiii.bannedplayer.BannedPlayer;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.util.HelperMethods;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.ZonedDateTime;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.util.HelperMethods.sendToAll;

public class UnbanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("unban"))
            return false;

        final Player playerSender = (Player) commandSender;

        if (!playerSender.hasPermission(STAFF_PERMISSION)) {
            playerSender.sendMessage(ERROR_COLOR + "You must be staff to access this command.");
            return false;
        }

        if (args.length != 1) {
            playerSender.sendMessage(ERROR_COLOR + "The proper usage of the command is: " + COMMAND_COLOR + "/unban (username)" + ERROR_COLOR + ".");
            return false;
        }

        String name = args[0];

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "The player you are looking for does not exist.");
            return false;
        }

        if (Staff.isStaff(offlinePlayer.getUniqueId())) {
            playerSender.sendMessage(ERROR_COLOR + "The player is a staff member and cannot be banned.");
            return false;
        }

        boolean isBanned = FileManager.isBanned(offlinePlayer.getUniqueId());

        if (!isBanned) {
            playerSender.sendMessage(ERROR_COLOR + "That player is not banned.");
            return false;
        }


        FileManager.unban(offlinePlayer.getUniqueId());
        playerSender.sendMessage(ChatColor.RED + "You have unbanned " + PLAYER_COLOR + offlinePlayer.getName() + ChatColor.RED + ".");


        return true;
    }
}
