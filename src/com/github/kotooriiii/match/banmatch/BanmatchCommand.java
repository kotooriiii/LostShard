package com.github.kotooriiii.match.banmatch;

import com.github.kotooriiii.match.Match;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class BanmatchCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (!(sender instanceof Player))
            return false;

        if (!cmd.getName().equalsIgnoreCase("banmatch"))
            return false;

        final Player playerSender = (Player) sender;

        if (!playerSender.hasPermission(STAFF_PERMISSION)) {
            playerSender.sendMessage(ERROR_COLOR + "You must be staff to access this command.");
            return false;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("cancel")) {


                if (Match.hasActiveMatch()) {
                    Match bm = Match.getActiveMatch();
                    bm.cancel(playerSender);

                    playerSender.sendMessage(STANDARD_COLOR + "You have canceled the match.");
                } else {
                    if(Match.getMatchCreatorMap().containsKey(playerSender.getUniqueId()))
                        playerSender.sendMessage(STANDARD_COLOR + "Canceled creation of match.");
                    else
                    playerSender.sendMessage(STANDARD_COLOR + "No active match able to be canceled.");
                }
                Match.getMatchCreatorMap().remove(playerSender.getUniqueId());

                return false;
            }
        }

        if (args.length != 2) {
            playerSender.sendMessage(ERROR_COLOR + "The proper usage of the command is: " + COMMAND_COLOR + "/bm (username) (username)" + ERROR_COLOR + ".");
            return false;
        }

        if(Match.hasActiveMatch())
        {
            playerSender.sendMessage(ERROR_COLOR + "You must wait for the other match to end to use this command. Or, you can use /bm cancel");
            return false;
        }

        String fighterA = args[0];
        String fighterB = args[1];


        OfflinePlayer offlineFighterA = Bukkit.getOfflinePlayer(fighterA);
        OfflinePlayer offlinePlayerFighterB = Bukkit.getOfflinePlayer(fighterB);

        if(offlineFighterA.equals(offlinePlayerFighterB))
        {
            playerSender.sendMessage(ERROR_COLOR + "Can't duel your clones.");
            return false;
        }

        if(!offlineFighterA.hasPlayedBefore() && !offlineFighterA.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "The player, " + PLAYER_COLOR + fighterA + ERROR_COLOR + ", you are looking for does not exist.");
            return false;
        }

        if(!offlinePlayerFighterB.hasPlayedBefore() && !offlinePlayerFighterB.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "The player, " + PLAYER_COLOR + fighterB + ERROR_COLOR + ", you are looking for does not exist.");
            return false;
        }

        Banmatch banmatch = new Banmatch(offlineFighterA.getUniqueId(), offlinePlayerFighterB.getUniqueId());
        Match.getMatchCreatorMap().put(playerSender.getUniqueId(), banmatch);
        playerSender.sendMessage(STANDARD_COLOR + "Creating banmatch session...");
        playerSender.sendMessage(STANDARD_COLOR + "What is the armor type? (diamond,gold,iron,chainmail)");

        return false;
    }
}