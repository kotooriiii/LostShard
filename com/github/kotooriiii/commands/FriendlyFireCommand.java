package com.github.kotooriiii.commands;

import com.github.kotooriiii.clans.Clan;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
                        playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");

                    if (clan.getFriendlyFire()) {
                        clan.broadcast(ChatColor.GREEN + "Clan friendly fire has been disabled.");
                    } else {
                        clan.broadcast(ChatColor.GREEN + "Clan friendly fire has been enabled.");
                    }
                    clan.setFriendlyFire(!clan.getFriendlyFire());

                } else {
                    playerSender.sendMessage("You provided too many arguments: /ff");
                }
            }
        }


        //end of commands
        return true;
    }
}
