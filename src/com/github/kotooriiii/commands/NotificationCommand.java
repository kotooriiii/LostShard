package com.github.kotooriiii.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NotificationCommand implements CommandExecutor {

    private final static HashSet<UUID> preferNotToBePingedMap = new HashSet();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("notification"))
            return false;

        Player player = (Player) commandSender;

        if (preferNotToBePingedMap.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.GOLD + "You have turned on notifications for alert pings.");
            preferNotToBePingedMap.remove(player.getUniqueId());
        } else {
            player.sendMessage(ChatColor.GOLD + "You have turned off notifications for alert pings.");
            preferNotToBePingedMap.add(player.getUniqueId());
        }
        return true;
    }

    public static boolean isPingable(Player player)
    {
        return isPingable(player.getUniqueId());
    }

    private static boolean isPingable(UUID uniqueId) {
        return !preferNotToBePingedMap.contains(uniqueId);
    }


}
