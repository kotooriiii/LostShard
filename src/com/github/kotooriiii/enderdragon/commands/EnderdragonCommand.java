package com.github.kotooriiii.enderdragon.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.enderdragon.EnderDragonManager;
import com.github.kotooriiii.util.HelperMethods;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.ZonedDateTime;

public class EnderdragonCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!command.getName().equalsIgnoreCase("enderdragon"))
            return false;

        final EnderDragonManager enderDragonManager = LostShardPlugin.getEnderDragonManager();

        if (enderDragonManager.getCooldownStrategy().isAlive()) {
            commandSender.sendMessage(ChatColor.RED + "The Ender Dragon is alive!");
            return false;
        } else {


            if(enderDragonManager.getCooldownStrategy().isIllusivelyAlive())
            {
                commandSender.sendMessage(ChatColor.RED + "The Ender Dragon is alive!");
                return false;
            }

            final ZonedDateTime killDate = enderDragonManager.getCooldownStrategy().getNextSummonDate();

            if (killDate == null) {
                commandSender.sendMessage(ChatColor.RED + "The Ender Dragon is summoned in: " + ChatColor.YELLOW +  "UNKNOWN" + ChatColor.RED  + ".");

            } else {
                commandSender.sendMessage(ChatColor.RED + "The Ender Dragon is summoned in: " + ChatColor.YELLOW + HelperMethods.getTimeLeft(killDate) + ChatColor.RED  + ".");

            }
        }

        return false;
    }
}
