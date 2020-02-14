package com.github.kotooriiii.commands;

import com.github.kotooriiii.clans.Clan;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class FriendlyFireCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            Player playerSender = (Player) sender;
            UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "ff" command
            if (cmd.getName().equalsIgnoreCase("ff")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    Clan clan = Clan.getClan(playerUUID);
                    if (clan == null)
                        playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");

                    if (clan.isFriendlyFire()) {
                        clan.broadcast(STANDARD_COLOR + "Clan friendly fire has been disabled.");
                    } else {
                        clan.broadcast(STANDARD_COLOR + "Clan friendly fire has been enabled.");
                    }
                    clan.setFriendlyFire(!clan.isFriendlyFire());

                } else {
                    playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/ff" + ERROR_COLOR + ".");
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("This command is not yet optimized for the console. Bother the developer to add commands :)");
        } //end of console sending commands

        //end of commands
        return true;
    }
}
