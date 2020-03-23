package com.github.kotooriiii.commands;

import com.github.kotooriiii.util.HelperMethods;
import javafx.application.Platform;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.COMMAND_COLOR;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class MsgCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(commandSender instanceof Player)
        {
            final Player playerSender = (Player) commandSender;

            if(args.length < 2)
            {
                playerSender.sendMessage(ERROR_COLOR + "The proper usage of the command is: " + COMMAND_COLOR + "/msg (username) (message)" + ERROR_COLOR + ".");
                return false;
            }

            String name = args[0];

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if(!offlinePlayer.hasPlayedBefore())
            {
                playerSender.sendMessage(ERROR_COLOR + "The player you are looking for has never set foot in this server.");
                return false;
            }

            if(!offlinePlayer.isOnline())
            {
                playerSender.sendMessage(ERROR_COLOR + "The player you are looking for is not online.");
                return false;
            }

            Player receivingPlayer = offlinePlayer.getPlayer();

            String message = HelperMethods.stringBuilder(args, 1, " ");

            playerSender.sendMessage("[" + ChatColor.LIGHT_PURPLE + "MSG to " + receivingPlayer.getName() + ChatColor.WHITE + "] " + message);
            receivingPlayer.sendMessage("[" + ChatColor.LIGHT_PURPLE + "MSG" + ChatColor.WHITE + "] " + playerSender.getName() + ": " + message);
        }




        return true;
    }
}
