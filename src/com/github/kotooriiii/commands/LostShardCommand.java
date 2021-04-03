package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.google.TutorialSheet;
import com.github.kotooriiii.stats.Stat;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
                case "removestats":
                    for (File file : LostShardPlugin.plugin.getDataFolder().listFiles()) {

                        if (file.getName().equals("stats")) {
                            for (File statFile : file.listFiles()) {
                                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(statFile);
                                if (yaml.getBoolean("isGold")) {
                                    yaml.set("Stamina", 100.0f);
                                    yaml.set("Mana", 100.0f);
                                    yaml.set("MaxMana", 100.0f);
                                    yaml.set("MaxStamina", 100.0f);
                                    yaml.set("Private", false);
                                    yaml.set("Spawn", null);
                                    yaml.set("EpochMillis", 0);
                                    try {
                                        yaml.save(statFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    continue;
                                }
                                statFile.delete();
                            }
                        }
                    }
                    commandSender.sendMessage(STANDARD_COLOR + "Cleaned stats.");

                    break;
                case "cleartitles":
                    final Collection<Stat> values = Stat.getStatMap().values();
                    for (Stat stat : values) {
                        if(stat.isGold())
                            continue;
                        else {
                            stat.setTitle("");
                            stat.setGold(false);
                        }
                    }
                    
                    commandSender.sendMessage(STANDARD_COLOR + "Cleaned stats.");

                    break;
                default:
            }
        }
        return true;
    }
}
