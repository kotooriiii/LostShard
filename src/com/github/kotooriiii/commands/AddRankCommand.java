package com.github.kotooriiii.commands;

import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.ranks.RankType;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class AddRankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("addrank")) {
                //No arguments regarding this command

                if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                    playerSender.sendMessage(ERROR_COLOR + "You don't have permission to add a rank to a player. You must be a staff member in order to access these set of commands.");
                    return false;
                }

                if (args.length != 2) {
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean to add a rank to a player? /addrank (username) (rankName)");
                    return false;
                }

                String possibleName = args[0];
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(possibleName);

                if(!offlinePlayer.hasPlayedBefore())
                {
                    playerSender.sendMessage(ERROR_COLOR + "The player you are searching for has never played on this server before");
                    return false;
                }

                String rankName = HelperMethods.stringBuilder(args, 1, " ");
                RankType rankType = RankType.matchRankType(rankName);

                if(rankType == null)
                {
                    playerSender.sendMessage(ERROR_COLOR + "That is not a valid rank type.");
                    return false;
                }


                RankPlayer rankPlayer = RankPlayer.wrap(offlinePlayer.getUniqueId());

                if(rankPlayer.getRankType().equals(rankType))
                {
                    playerSender.sendMessage(ERROR_COLOR + "The player is already this rank.");
                    return false;
                }

                rankPlayer.setRankType(rankType);
                for(Player player : Bukkit.getOnlinePlayers())
                player.sendMessage(PLAYER_COLOR + offlinePlayer.getName() + STANDARD_COLOR + " has now been promoted to " + rankType.getName() + ".");

            }
        }

        return true;
    }
}
