package com.github.kotooriiii.channels.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.ChannelStatus;
import com.github.kotooriiii.channels.ChannelManager;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class GlobalCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("global")) {
                ChannelManager channelManager = LostShardPlugin.getChannelManager();
                //No arguments regarding this command
                if (args.length == 0) {
                    channelManager.joinChannel(playerSender, ChannelStatus.GLOBAL);
                    playerSender.sendMessage(STANDARD_COLOR + "You have switched to global chat.");


                } else {
                    String message = HelperMethods.stringBuilder(args, 0, " ");

                    ChannelStatus status = channelManager.getChannel(playerSender);
                    channelManager.joinChannel(playerSender, ChannelStatus.GLOBAL);
                    playerSender.chat(message);
                    channelManager.joinChannel(playerSender, status);
                }
            }
        }
        return true;
    }
}
