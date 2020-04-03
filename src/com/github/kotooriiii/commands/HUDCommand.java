package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Bank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class HUDCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("hud")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    if(hudContainer.contains(playerUUID))
                    {
                        playerSender.sendMessage(STANDARD_COLOR + "You have toggled the HUD on.");
                        hudContainer.remove(playerUUID);
                    } else {
                        playerSender.sendMessage(STANDARD_COLOR + "You have toggled the HUD off.");
                        hudContainer.add(playerUUID);
                    }

                } else {
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean to toggle the " + COMMAND_COLOR + "/hud" + ERROR_COLOR + "?");
                }
            }
        }
        return true;
    }
}
