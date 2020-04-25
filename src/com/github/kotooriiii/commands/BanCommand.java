package com.github.kotooriiii.commands;

import com.github.kotooriiii.bannedplayer.BannedPlayer;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.match.banmatch.Banmatch;
import com.github.kotooriiii.muted.MutedPlayer;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.util.HelperMethods;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.ZonedDateTime;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.sendToAll;

public class BanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("ban"))
            return false;

        final Player playerSender = (Player) commandSender;

        if (!playerSender.hasPermission(STAFF_PERMISSION)) {
            playerSender.sendMessage(ERROR_COLOR + "You must be staff to access this command.");
            return false;
        }

        if (args.length == 0) {
            playerSender.sendMessage(ERROR_COLOR + "The proper usage of the command is: " + COMMAND_COLOR + "/ban (username) [optional: 1d 3min]" + ERROR_COLOR + ".");
            return false;
        } else if (args.length >= 1) {
            String name = args[0];

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                playerSender.sendMessage(ERROR_COLOR + "The player you are looking for does not exist.");
                return false;
            }

            if (Staff.isStaff(playerSender.getUniqueId())) {
                if (offlinePlayer.isOnline()) {
                    offlinePlayer.getPlayer().sendMessage(ERROR_COLOR + playerSender.getName() + " tried to ban you.");

                    playerSender.sendMessage(ERROR_COLOR + "The player is a staff member and cannot be banned.");

                }
                return false;

            }

            boolean isBanned = FileManager.isBanned(offlinePlayer.getUniqueId());

            if (isBanned) {
                playerSender.sendMessage(ERROR_COLOR + "That player is already banned.");
                return false;
            }

            if (args.length == 1) {
                BannedPlayer bannedPlayer = new BannedPlayer(offlinePlayer.getUniqueId(), ZonedDateTime.now().withYear(0), "You are banned.");
                bannedPlayer.save();
            } else {
                String timeProps = HelperMethods.stringBuilder(args, 1, " ");
                if (!matchesRegex(timeProps)) {
                    TextComponent tc = new TextComponent(STANDARD_COLOR + "Could not parse the time. Try using the ban command again (Quick Example: 1h 25m 30s)");
                    TextComponent component = new TextComponent("\n" + ChatColor.YELLOW + "Example 1: Hover to view another example.");
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "Full arguments: " + COMMAND_COLOR + "'/ban <player> 1y 5mo 2w 0d 0h 0min 0s'\n" + STANDARD_COLOR + "Equivalent to: " + COMMAND_COLOR + "1 year, 5 months, 2 weeks, 0 days, 0 hours, 0 minutes, 0 seconds" + STANDARD_COLOR + ".\n\nFully uses all arguments provided.").create()));
                    TextComponent component2 = new TextComponent("\n" + ChatColor.YELLOW + "Example 2: Hover to view another example.");
                    component2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "Preferred arguments: " + COMMAND_COLOR + "'/ban <player> 1y 9mo 4s'\n" + STANDARD_COLOR + "Equivalent to: " + COMMAND_COLOR + "1 year, 9 months, 4 seconds" + STANDARD_COLOR + ".\n\nNOTE: The other time identifiers not listed here will be by default, '0'.").create()));
                    TextComponent component3 = new TextComponent("\n" + ChatColor.YELLOW + "Example 3: Hover to view another example.");
                    component3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "One-way argument: " + COMMAND_COLOR + "'" + "/ban <player>" + "'\n" + STANDARD_COLOR + "Equivalent to: " + COMMAND_COLOR + "Banned indefinitely" + STANDARD_COLOR + ".").create()));
                    tc.addExtra(component);
                    tc.addExtra(component2);
                    tc.addExtra(component3);
                    playerSender.spigot().sendMessage(tc.duplicate());
                    return false;
                }

                ZonedDateTime bannedZDT = HelperMethods.toZDT(toProperties(timeProps));
                BannedPlayer bannedPlayer = new BannedPlayer(offlinePlayer.getUniqueId(), bannedZDT, "You are banned.");

                bannedPlayer.save();
            }
            sendToAll(ChatColor.GREEN + offlinePlayer.getName() + " has been banned.");
            if (offlinePlayer.isOnline())
                offlinePlayer.getPlayer().kickPlayer("You are banned.");

        }


        return true;
    }

    public static boolean matchesRegex(String message) {
        String[] properties = message.split(" ");

        for (String property : properties) {
            if (!property.matches("[0-9]+y|[0-9]+mo|[0-9]+w|[0-9]+d|[0-9]+h|[0-9]+min|[0-9]+s")) {
                return false;
            }
        }
        return true;
    }

    public static int[] toProperties(String message) {
        String[] properties = message.toLowerCase().split(" ");
        int[] numproperties = new int[]
                {0, 0, 0, 0, 0, 0, 0}; //year,month,week,day,hour,minute,second
        for (String property : properties) {
            String identifier = property.replaceAll("[0-9]+", "");
            String timeString = property.replaceAll("[A-Za-z]", "");
            int time = Integer.parseInt(timeString);
            switch (identifier) {
                case "y":
                    numproperties[0] = time;
                    break;
                case "mo":
                    numproperties[1] = time;
                    break;
                case "w":
                    numproperties[2] = time;
                    break;
                case "d":
                    numproperties[3] = time;
                    break;
                case "h":
                    numproperties[4] = time;
                    break;
                case "min":
                    numproperties[5] = time;
                    break;
                case "s":
                    numproperties[6] = time;
                    break;
                default:
                    return null;
            }
        }

        return numproperties;
    }
}
