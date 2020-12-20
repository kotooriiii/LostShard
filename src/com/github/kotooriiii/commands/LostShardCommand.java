package com.github.kotooriiii.commands;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.google.TutorialSheet;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Random;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class LostShardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!command.getName().equalsIgnoreCase("lostshard"))
            return false;

        if (!commandSender.hasPermission(STAFF_PERMISSION)) {
            commandSender.sendMessage(ERROR_COLOR + "You must be staff to access this command.");
            return false;
        }
        if (args.length == 0) {
            commandSender.sendMessage("To delete LostShard content: " + "/ls reset");
            return false;
        } else {

            switch (args[0].toLowerCase()) {
                case "reset":
                    if (!(commandSender instanceof ConsoleCommandSender)) {
                        commandSender.sendMessage(ERROR_COLOR + "This command is only allowed to console.");
                        return false;
                    }
                    FileManager.reset();
                    break;
                case "tutorial-append":
                    Random ran = new Random();
                    TutorialSheet.getInstance().append(UUID.randomUUID(), "DummyName", ran.nextBoolean(), "DummyChapter", ran.nextInt(500), ran.nextInt(500), ran.nextInt(500), true, true, ran.nextInt(500));
                    commandSender.sendMessage(STANDARD_COLOR + "Appended to sheets.");
                    break;

                default:
            }
        }
        return true;
    }
}
