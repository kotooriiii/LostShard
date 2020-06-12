package com.github.kotooriiii.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class PlaytimeCommand implements CommandExecutor {

    private final static HashSet<UUID> preferNotToBePingedMap = new HashSet();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("playtime"))
            return false;

        if (!commandSender.hasPermission(STAFF_PERMISSION))
            return false;

        Player player = (Player) commandSender;

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reset")) {
                if (args.length != 2) {
                    player.sendMessage(ChatColor.GOLD + "To reset a player's time played on the server: /playtime reset (username)");
                    return false;
                } else {
                    String name = args[1];
                    Player resettedPlayer = Bukkit.getPlayer(name);
                    if (resettedPlayer == null || !resettedPlayer.isOnline()) {
                        player.sendMessage(ERROR_COLOR + "The player you are trying to search for is not online");
                        return false;
                    }

                    //exists
                    player.sendMessage(ChatColor.GOLD + resettedPlayer.getName() + "'s time has been reset.");

                    resettedPlayer.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
                }
            } else if (args[0].equalsIgnoreCase("resetall")) {
                if (args.length != 1) {
                    player.sendMessage(ChatColor.GOLD + "To reset everyone's playtime: /playtime resetall");
                    return false;
                } else {
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {

                        offlinePlayer.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
                    }
                    player.sendMessage(ChatColor.GOLD + "Successfully reset everyone's time.");
                }
            }
        }

        return true;
    }
}
