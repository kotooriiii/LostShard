package com.github.kotooriiii.commands;

import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.util.HelperMethods;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class MarkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("mark")) {

                if (args.length == 0) {
                    sendPage(1, playerSender);
                } else if (args.length >= 1) {
                    switch (args[0].toLowerCase()) {
                        case "give":
                            if(args.length < 3)
                            {
                                playerSender.sendMessage(STANDARD_COLOR + "Did you mean to give a mark to a player? " + COMMAND_COLOR + "/marks give (username) (mark name).");
                            } else {
                                String playerName = args[1];
                                String markName = HelperMethods.stringBuilder(args, 2, " ");
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                                if(!offlinePlayer.isOnline())
                                {
                                    playerSender.sendMessage(ERROR_COLOR + "The player must be online.");
                                    return false;
                                }

                                Player playerReceivingMark = offlinePlayer.getPlayer();

                                int distanceToGiveMark = 10; //exclude
                                if(!playerReceivingMark.getWorld().equals(playerSender.getWorld()) || playerReceivingMark.getLocation().distance(playerSender.getLocation()) > distanceToGiveMark)
                                {
                                    playerSender.sendMessage(ERROR_COLOR + "The player must be within " + distanceToGiveMark + " blocks.");
                                    return false;
                                }

                                MarkPlayer markPlayerGiving = MarkPlayer.wrap(playerUUID);
                                MarkPlayer markPlayerReceiving = MarkPlayer.wrap(playerReceivingMark.getUniqueId());

                                if(!markPlayerGiving.hasMark(markName))
                                {
                                    playerSender.sendMessage(ERROR_COLOR + "You don't have a mark by that name.");
                                    return false;
                                }

                                MarkPlayer.Mark mark = markPlayerGiving.getMark(markName);

                                if(markPlayerReceiving.hasMark(mark.getName()))
                                {
                                    playerSender.sendMessage(ERROR_COLOR + "The player already has a mark by this name.");
                                    return false;
                                }

                                RankPlayer rankPlayer = RankPlayer.wrap(playerReceivingMark.getUniqueId());

                                if(markPlayerReceiving.getMarks().length == rankPlayer.getRankType().getMaxMarksNum())
                                {
                                    playerSender.sendMessage(ERROR_COLOR + "The player is already at max capacity for marks.");
                                    return false;
                                }
                                /*

                                The receiving player is online
                                The player is in the same world AND distance is within 5 blocks
                                The sending player has the mark by that name
                                The receiving player already has a mark by that mark name.
                                The receiving player is not at max cap.


                                 */

                                markPlayerReceiving.addMark(mark.getName(), mark.getLocation());
                                markPlayerGiving.removeMark(mark.getName());

                                playerSender.sendMessage(ChatColor.GOLD + "You have given the mark, \"" + mark.getName() + "\", to "+ playerReceivingMark.getName() + ChatColor.GOLD + ".");
                                playerReceivingMark.sendMessage(ChatColor.GOLD+ "You have received the mark, \"" + mark.getName() + "\", from " + playerSender.getName() + ChatColor.GOLD + ".");

                            }
                            break;
                        case "help":
                            break;
                        case "remove":
                            if (args.length == 1) {
                                //Make a chat name
                            } else {
                                String name = HelperMethods.stringBuilder(args, 1, " ");
                                //Check if name is already taken
                                if (!MarkPlayer.hasMarks(playerUUID)) {
                                    playerSender.sendMessage(ERROR_COLOR + "You don't have any marks.");
                                    return false;
                                }

                                MarkPlayer markPlayer = MarkPlayer.wrap(playerUUID);
                                if (!markPlayer.hasMark(name)) {
                                    playerSender.sendMessage(ERROR_COLOR + "You do not have a mark by this name.");
                                    return true;
                                }
                                playerSender.sendMessage(ChatColor.GOLD + "You have removed the mark \"" + markPlayer.getMark(name).getName() + "\".");
                                markPlayer.removeMark(name);
                            }
                            break;
                        default:

                            String numberString = args[0];
                            if (!NumberUtils.isNumber(numberString) || numberString.contains(".")) {

                                playerSender.sendMessage(ERROR_COLOR + "You must use a positive integer for a page.");
                                return false;
                            }

                            int page = Integer.parseInt(numberString);

                            if (page <= 0) {
                                playerSender.sendMessage(ERROR_COLOR + "You must use a positive integer for a page.");
                                return false;
                            }
                            sendPage(page, playerSender);
                    }

                }
            }
        }
        return true;
    }

    public void sendHelp(Player player)
    {

    }

    public void sendPage(int page, Player playerSender) {
        UUID playerUUID = playerSender.getUniqueId();

        final int amtOfMarksPerPage = 8;

        int size;
        MarkPlayer.Mark[] marks;
        MarkPlayer.Mark[] premadeMarks;
        if (!MarkPlayer.hasMarks(playerUUID)) {
            size = 0;
            marks = null;
        } else {
            MarkPlayer markPlayer = MarkPlayer.wrap(playerUUID);
            marks = markPlayer.getMarks();
            size = marks.length;
        }
        premadeMarks = MarkPlayer.wrap(playerUUID).getPremadeMarks();

        int pages = (int) Math.ceil((double) size / amtOfMarksPerPage);
        if (pages == 0)
            pages = 1;
        int pageCounter = page;
        int markCounter = 0;


        if (page > pages) {
            playerSender.sendMessage(ERROR_COLOR + "You do not have any more mark pages.");
            return;
        }

        playerSender.sendMessage(ChatColor.GOLD + "-" + playerSender.getName() + "'s Marks-");

        playerSender.sendMessage(ChatColor.GOLD + "Pg " + pageCounter + " of " + pages + " (" + size + " of " + RankPlayer.wrap(playerUUID).getRankType().getMaxMarksNum() + " marks used)");

        for (int i = (page - 1) * amtOfMarksPerPage; i < size; i++) {
            if (markCounter == amtOfMarksPerPage) {
                return;
            }

            if (marks == null)
                break;

            playerSender.sendMessage(ChatColor.WHITE + "- " + marks[i].getName() + " - " + "(" + marks[i].getLocation().getBlockX() + "," + marks[i].getLocation().getBlockY() + "," + marks[i].getLocation().getBlockZ() + ")");

            markCounter++;
        }

        playerSender.sendMessage(ChatColor.GOLD + "-Premade Marks-");
        for(MarkPlayer.Mark mark : premadeMarks)
        {
            if(mark.getName().equalsIgnoreCase("random"))
            {
                playerSender.sendMessage(ChatColor.WHITE + "- " + mark.getName() + " - " + "(?,?,?)");
                continue;
            }
            playerSender.sendMessage(ChatColor.WHITE + "- " + mark.getName() + " - " + "(" + mark.getLocation().getBlockX() + "," + mark.getLocation().getBlockY() + "," + mark.getLocation().getBlockZ() + ")");
        }



    }


}
