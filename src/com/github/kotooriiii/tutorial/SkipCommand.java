package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.*;

public class SkipCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if(!cmd.getName().equalsIgnoreCase("skip"))
            return false;

        if(!LostShardPlugin.isTutorial())
        {
            sender.sendMessage(ERROR_COLOR + "This command is only supported in the tutorial server.");
            return false;
        }

        if(!(sender instanceof Player))
        {
            sender.sendMessage(ERROR_COLOR + "You must be a player to use this command.");
            return false;
        }

        Player player = (Player) sender;

        /*
        Player command
        Skip command
        Is tutorial
         */

        sender.sendMessage(STANDARD_COLOR + "Completing the tutorial...");
        LostShardPlugin.getTutorialManager().removeTutorial(player.getUniqueId(), TutorialCompleteType.SKIP);
        return false;
    }
}
