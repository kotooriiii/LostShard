package com.github.kotooriiii.skills.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.skill_listeners.TamingListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import static com.github.kotooriiii.data.Maps.COMMAND_COLOR;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PetsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("pets")) {
                if (args.length == 0) {
                    sendHelp(playerSender);

                } else {
                    switch (args[0].toLowerCase()) {
                        case "kill":
                            killPets(playerSender);
                            break;
                        default:
                        case "help":
                            sendHelp(playerSender);
                            break;
                        case "tp":
                        case "teleport":
                            teleport(playerSender);
                            break;
                    }

                }


            }
        }
        return true;
    }

    private void sendHelp(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "------Pets Help------");
        playerSender.sendMessage(COMMAND_COLOR + "/pets tp");
        playerSender.sendMessage(COMMAND_COLOR + "/pets kill");
        playerSender.sendMessage(COMMAND_COLOR + "/pets help");


    }

    private void killPets(Player playerSender)
    {

        Wolf[] wolves = TamingListener.getWolves(playerSender);

        if (wolves.length == 0) {
            playerSender.sendMessage(ERROR_COLOR + "You have no pets.");
            return;
        }

        for (Wolf wolf : wolves) {
            wolf.damage(wolf.getHealth()*2);
        }
        playerSender.sendMessage(ChatColor.GRAY + "You have killed your pets.");
    }

    private void teleport(Player playerSender) {
        if ((int) LostShardPlugin.getSkillManager().getSkillPlayer(playerSender.getUniqueId()).getActiveBuild().getTaming().getLevel() < 50) {
            playerSender.sendMessage(ERROR_COLOR + "You must be at least level 50 to teleport your pets.");
            return;
        }


        Wolf[] wolves = TamingListener.getWolves(playerSender);

        if (wolves.length == 0) {
            playerSender.sendMessage(ERROR_COLOR + "You have no pets.");
            return;
        }

        for (Wolf wolf : wolves) {
            wolf.teleport(playerSender);
        }
        playerSender.sendMessage(ChatColor.GRAY + "You have teleported your pets to you.");
    }

}
