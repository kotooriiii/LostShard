package com.github.kotooriiii.commands;

import com.github.kotooriiii.files.FileManager;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class LostShardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!command.getName().equalsIgnoreCase("lostshard"))
            return false;

        if (!commandSender.hasPermission("lostshard")) {
            commandSender.sendMessage(ERROR_COLOR + "You must be staff to access this command.");
            return false;
        }
        if (args.length == 0) {
            commandSender.sendMessage("To delete LostShard content: " + "/ls reset");
            return false;
        } else {

            switch (args[0].toLowerCase()) {
                case "reset":
                    if(!(commandSender instanceof ConsoleCommandSender))
                    {
                        commandSender.sendMessage(ERROR_COLOR + "This command is only allowed to console.");
                        return  false;
                    }
                    FileManager.reset();
                    break;
                default:
            }
        }
        return true;
    }
}
