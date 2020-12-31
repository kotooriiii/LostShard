package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;


public class MeditateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("meditate")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    if(Stat.getMeditatingPlayers().contains(playerUUID))
                    {
                        playerSender.sendMessage(ChatColor.GOLD + "You are already meditating...");
                        return true;
                    }
                    playerSender.sendMessage(ChatColor.GOLD + "You begin meditating.");
                    Stat.getMeditatingPlayers().add(playerUUID);

                } else {
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean " + COMMAND_COLOR + "/meditate" + ERROR_COLOR + "?");
                }
            }
        }
        return true;
    }

}
