package com.github.kotooriiii.commands;

import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.util.HelperMethods;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.core.pattern.MarkerPatternConverter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

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


    public void sendPage(int page, Player playerSender) {
        UUID playerUUID = playerSender.getUniqueId();

        final int amtOfMarksPerPage = 3;

        int size;
        MarkPlayer.Mark[] marks;
        if (!MarkPlayer.hasMarks(playerUUID)) {
            size = 0;
            marks = null;
        } else {
            MarkPlayer markPlayer = MarkPlayer.wrap(playerUUID);
            marks = markPlayer.getMarks();
            size = marks.length;
        }

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

        playerSender.sendMessage(ChatColor.GOLD + "Pg " + pageCounter + " of " + pages + " (" + size + " of " + 3 + " marks used)");

        for (int i = (page - 1) * amtOfMarksPerPage; i < size; i++) {
            if (markCounter == amtOfMarksPerPage) {
                pageCounter++;
                markCounter = 0;
                return;
            }

            if (marks == null)
                break;

            playerSender.sendMessage(ChatColor.WHITE + "- " + marks[i].getName() + " - " + "(" + marks[i].getX() + "," + marks[i].getY() + "," + marks[i].getZ() + ")");

            markCounter++;
        }
    }


}
