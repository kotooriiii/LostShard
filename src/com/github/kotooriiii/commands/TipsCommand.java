package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.tips.TipsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class TipsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "tips" command
            if (cmd.getName().equalsIgnoreCase("tips")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    TipsManager manager = LostShardPlugin.getTipsManager();
                    if(manager.isBlacklist(playerUUID))
                    {
                        playerSender.sendMessage(STANDARD_COLOR + "You have toggled tips on.");
                        manager.unblacklist(playerUUID);
                    } else {
                        playerSender.sendMessage(STANDARD_COLOR + "You have toggled tips off.");
                        manager.blacklist(playerUUID);
                    }

                } else {
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean to toggle the " + COMMAND_COLOR + "/tips" + ERROR_COLOR + "?");
                }
            }
        }
        return true;
    }
}
