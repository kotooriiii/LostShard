package com.github.kotooriiii.channels.commands;

import com.github.kotooriiii.channels.listeners.ChatChannelListener;
import com.github.kotooriiii.commands.NotificationCommand;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.COMMAND_COLOR;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class ReplyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("reply"))
            return false;
        final Player playerSender = (Player) commandSender;

        if (args.length == 0) {
            playerSender.sendMessage(ERROR_COLOR + "The proper usage of the command is: " + COMMAND_COLOR + "/reply (message)" + ERROR_COLOR + ".");
            return false;
        }

        String message = HelperMethods.stringBuilder(args, 0, " ");

        UUID toReplyToUUID = MsgCommand.getUUIDToReplyTo(playerSender.getUniqueId());

        if (toReplyToUUID == null) {
            playerSender.sendMessage(ERROR_COLOR + "No one has sent you a message yet.");
            return false;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(toReplyToUUID);

        if (!offlinePlayer.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "The player you are looking for is not online.");
            return false;
        }

        Player receivingPlayer = offlinePlayer.getPlayer();

        playerSender.sendMessage("[" + ChatColor.LIGHT_PURPLE + "MSG to " + receivingPlayer.getName() + ChatColor.WHITE + "] " + message);
        receivingPlayer.sendMessage("[" + ChatColor.LIGHT_PURPLE + "MSG" + ChatColor.WHITE + "] " + playerSender.getName() + ": " + message);
        if (NotificationCommand.isPingable(receivingPlayer))
            receivingPlayer.playSound(receivingPlayer.getLocation(), ChatChannelListener.PING_SOUND, 10, ChatChannelListener.PING_PITCH);
        MsgCommand.updateMap(receivingPlayer.getUniqueId(), playerSender.getUniqueId());

        return true;
    }


}
