package com.github.kotooriiii.skills.commands;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.skills.listeners.TamingListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.text.DecimalFormat;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.COMMAND_COLOR;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PetsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("pets")) {
                Wolf[] wolves = TamingListener.getWolves(playerSender);
                for(Wolf wolf : wolves)
                {
                    wolf.teleport(playerSender);
                }
                playerSender.sendMessage(ChatColor.GRAY + "You have teleported your pets to you.");
            }
        }
        return true;
    }

}