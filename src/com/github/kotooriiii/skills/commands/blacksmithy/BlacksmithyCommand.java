package com.github.kotooriiii.skills.commands.blacksmithy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.COMMAND_COLOR;

public class BlacksmithyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!command.getName().equalsIgnoreCase("blacksmithy"))
            return false;

        sendHelp(commandSender);

        return false;
    }
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "-Blacksmithy Help-");

        sender.sendMessage(COMMAND_COLOR + "/repair" + ChatColor.YELLOW + " (repairs item in hand)");
        sender.sendMessage(COMMAND_COLOR + "/smelt" + ChatColor.YELLOW + " (smelts item in hand)");
        sender.sendMessage(COMMAND_COLOR + "/enhance" + ChatColor.YELLOW + " (enhances tool in hand)");
        sender.sendMessage(COMMAND_COLOR + "/sharpen" + ChatColor.YELLOW + " (sharpens sword in hand)");
        sender.sendMessage(COMMAND_COLOR + "/harden" + ChatColor.YELLOW + " (hardens armor in hand)");
        sender.sendMessage(COMMAND_COLOR + "/power" + ChatColor.YELLOW + " (enhances bow in hand)");

    }
}
