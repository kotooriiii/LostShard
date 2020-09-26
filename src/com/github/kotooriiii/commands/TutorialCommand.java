package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.tutorial.TutorialBook;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class TutorialCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("tutorial"))
            return false;

        final Player playerSender = (Player) commandSender;


        if (args.length == 0) {
            playerSender.sendMessage("You've already completed the tutorial.");
            return false;
        }

        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "staff":
                    if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                        playerSender.sendMessage(ERROR_COLOR + "You must be staff to access this command.");
                        return false;
                    }
                    if (args.length == 1) {
                        playerSender.sendMessage("You can advance to next with : /tutorial staff advance");
                        return false;
                    } else if (args.length >= 2) {
                        if (!LostShardPlugin.isTutorial()) {
                            playerSender.sendMessage("Not a tutorial server.");
                            return false;
                        }
                        if (args[1].equalsIgnoreCase("advance")) {
                            TutorialBook book = LostShardPlugin.getTutorialManager().wrap(playerSender.getUniqueId());
                            if (book == null) {
                                playerSender.sendMessage("Cannot find the tutorial progress of this player.");
                                return false;
                            }

                            if (book.getCurrentChapter() == null) {
                                playerSender.sendMessage("Current chapter of the tutorial is null.");
                                return false;
                            }

                            playerSender.sendMessage("Advanced tutorial progress by one chapter.");
                            book.getCurrentChapter().setComplete();
                            return false;
                        }
                        playerSender.sendMessage("No command found.");
                        return false;
                    }
            }

        }

        return true;
    }
}
