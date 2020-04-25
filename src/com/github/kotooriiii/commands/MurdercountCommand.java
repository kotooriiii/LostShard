package com.github.kotooriiii.commands;

import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.ranks.RankType;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.util.HelperMethods;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class MurdercountCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("murdercount")) {
                //No arguments regarding this command

                if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                    playerSender.sendMessage(ERROR_COLOR + "You don't have permission to set murder count. You must be a staff member in order to access these set of commands.");
                    return false;
                }

                if (args.length != 2) {
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean to set a murder count to a player? /murdercount (username) (number)");
                    return false;
                }

                String possibleName = args[0];
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(possibleName);

                if(!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline())
                {
                    playerSender.sendMessage(ERROR_COLOR + "The player you are searching for has never played on this server before.");
                    return false;
                }

                if(!NumberUtils.isNumber(args[1]) || args[1].contains("."))
                {
                    playerSender.sendMessage(ERROR_COLOR + "Must be an integer-based number for murder count.");
                    return false;
                }

                StatusPlayer statusPlayer = StatusPlayer.wrap(offlinePlayer.getUniqueId());

                int killsNum = Integer.parseInt(args[1]);

                if(killsNum < 5)
                {
                    statusPlayer.setStatus(Status.WORTHY);
                } else {
                    statusPlayer.setStatus(Status.EXILED);
                }

                statusPlayer.setKills(killsNum);
                playerSender.sendMessage(PLAYER_COLOR + offlinePlayer.getName() + STANDARD_COLOR + "'s kills have been set to " + killsNum + ".");



            }
        }

        return true;
    }
}
