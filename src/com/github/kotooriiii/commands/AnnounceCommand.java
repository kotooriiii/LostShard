package com.github.kotooriiii.commands;

import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class AnnounceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            if (!Staff.isStaff(playerUUID)) {
                playerSender.sendMessage(ERROR_COLOR + "No permission to announce.");
                return false;
            }
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("announce")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    playerSender.sendMessage(ERROR_COLOR + "Wrong syntax. Try /announce (message)");
                } else {
                    String message = HelperMethods.stringBuilder(args, 0, " ");
                    playerSender.sendMessage(ChatColor.GREEN + "[Server] " + message);
                }
            }
        }
        return true;
    }
}
