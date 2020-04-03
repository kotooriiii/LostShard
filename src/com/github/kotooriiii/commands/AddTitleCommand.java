package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class AddTitleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("addtitle")) {
                //No arguments regarding this command

                if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                    playerSender.sendMessage(ERROR_COLOR + "You don't have permission to add a title to a player. You must be a staff member in order to access these set of commands.");
                    return false;
                }

                if (args.length < 2) {
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean to add a title to a player? /addtitle (username) (title)");
                    return false;
                }

                String possibleName = args[0];
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(possibleName);

                if(!offlinePlayer.hasPlayedBefore())
                {
                    playerSender.sendMessage(ERROR_COLOR + "The player you are searching for has never played on this server before");
                    return false;
                }

                String title = HelperMethods.stringBuilder(args, 1, " ");
                Stat stat = Stat.wrap(offlinePlayer.getUniqueId());
                if(!title.equalsIgnoreCase("null")) {
                    stat.setTitle(title);
                    playerSender.sendMessage(PLAYER_COLOR + offlinePlayer.getName() + STANDARD_COLOR + " will now be referred to " + title + ".");
                } else {
                    stat.setTitle("");
                    playerSender.sendMessage(PLAYER_COLOR + offlinePlayer.getName() + STANDARD_COLOR + "'s title has been removed.");
                }

            }
        }

        return true;
    }
}
