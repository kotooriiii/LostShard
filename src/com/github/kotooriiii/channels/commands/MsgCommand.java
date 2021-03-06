package com.github.kotooriiii.channels.commands;

import com.github.kotooriiii.LostShardPlugin;
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

import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.COMMAND_COLOR;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class MsgCommand implements CommandExecutor {

    private static HashMap<UUID, UUID> lastMessageMap = new HashMap<UUID, UUID>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("msg"))
            return false;
        final Player playerSender = (Player) commandSender;

        if (args.length < 2) {
            playerSender.sendMessage(ERROR_COLOR + "The proper usage of the command is: " + COMMAND_COLOR + "/msg (username) (message)" + ERROR_COLOR + ".");
            return false;
        }

        String name = args[0];

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (!offlinePlayer.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "The player you are looking for is not online.");
            return false;
        }


        Player receivingPlayer = offlinePlayer.getPlayer();

        String message = HelperMethods.stringBuilder(args, 1, " ");

        playerSender.sendMessage("[" + ChatColor.LIGHT_PURPLE + "MSG to " + receivingPlayer.getName() + ChatColor.WHITE + "] " + message);
        if (!LostShardPlugin.getIgnoreManager().wrap(receivingPlayer.getUniqueId()).isIgnoring(playerSender.getUniqueId())) {
            receivingPlayer.sendMessage("[" + ChatColor.LIGHT_PURPLE + "MSG" + ChatColor.WHITE + "] " + playerSender.getName() + ": " + message);
            if (NotificationCommand.isPingable(receivingPlayer))
                receivingPlayer.playSound(receivingPlayer.getLocation(), ChatChannelListener.PING_SOUND, 10, ChatChannelListener.PING_PITCH);
        }


        updateMap(receivingPlayer.getUniqueId(), playerSender.getUniqueId());

        return true;
    }

    public static UUID getUUIDToReplyTo(UUID personExecutingCommand) {
        return lastMessageMap.get(personExecutingCommand);
    }

    public static void updateMap(UUID receive, UUID sent) {
        if (!LostShardPlugin.getIgnoreManager().wrap(receive).isIgnoring(sent))
            lastMessageMap.put(receive, sent); //only for person who messaged you
        lastMessageMap.put(sent, receive); // this is for convo based
    }
}
