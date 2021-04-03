package com.github.kotooriiii.commands;

import com.github.kotooriiii.listeners.EnderDragonLivesListener;
import com.github.kotooriiii.util.HelperMethods;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;

public class EnderdragonCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!command.getName().equalsIgnoreCase("enderdragon"))
            return false;

        if (EnderDragonLivesListener.isAlive()) {
            commandSender.sendMessage(ChatColor.RED + "The Ender Dragon is alive!");
            return false;
        } else {

            final ZonedDateTime killDate = EnderDragonLivesListener.getSummonDate();

            if (killDate == null) {
                commandSender.sendMessage(ChatColor.RED + "The Ender Dragon is summoned in: " + ChatColor.YELLOW +  "UNKNOWN" + ChatColor.RED  + ".");

            } else {
                commandSender.sendMessage(ChatColor.RED + "The Ender Dragon is summoned in: " + ChatColor.YELLOW + HelperMethods.getTimeLeft(killDate) + ChatColor.RED  + ".");

            }
        }

        return false;
    }
}
