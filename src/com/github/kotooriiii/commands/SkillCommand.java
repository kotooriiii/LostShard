package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.skills.SkillType;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class SkillCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("skills")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    playerSender.sendMessage(ChatColor.GOLD + "-" + playerSender.getName() + "'s Skills-");

                    SkillPlayer skillPlayer = SkillPlayer.wrap(playerUUID);
                    float skillNum = 0;
                    float maxSkillNum = 0;

                    for(SkillPlayer.Skill skill : skillPlayer.getSkills())
                    {
                        skillNum += skill.getLevel();
                        maxSkillNum += 100;
                    }

                    playerSender.sendMessage(ChatColor.YELLOW + "You currently have " + skillNum+ "/" + maxSkillNum + " skill points.");


                    for(SkillPlayer.Skill skill : skillPlayer.getSkills())
                    {
                        playerSender.sendMessage(ChatColor.YELLOW + skill.getType().getName() + ": " + ChatColor.WHITE + skill.getLevel());
                    }
                    return false;
                }

                switch (args[0].toLowerCase()) {

                    case "staff": //skills | staff <player> <skill> <level>

                        if(!playerSender.hasPermission(STAFF_PERMISSION))
                        {
                            playerSender.sendMessage(ERROR_COLOR + "You do not have staff permission to access this page.");
                            return false;
                        }

                        if(args.length != 4)
                        {
                            playerSender.sendMessage(STANDARD_COLOR + "To set the level of a player do: /skills staff (username) (skill) (level)");
                            return false;
                        }

                        String playerNameString = args[1];
                        String skillString = args[2];
                        String levelString = args[3];

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerNameString);
                        if(!offlinePlayer.hasPlayedBefore())
                        {
                            playerSender.sendMessage(ERROR_COLOR+ "The player you have listed does not have a Skills account (does the player exist?).");
                            return false;
                        }

                        if(!SkillType.isSkill(skillString))
                        {
                            playerSender.sendMessage(ERROR_COLOR+"You must set a valid skill type.");
                            return false;
                        }

                        SkillType type = SkillType.valueOf(skillString.toUpperCase());

                        if(!NumberUtils.isNumber(levelString))
                        {
                            playerSender.sendMessage(ERROR_COLOR+"You must set a number based level.");
                            return false;
                        }

                        float level = Float.valueOf(levelString);

                        SkillPlayer.wrap(offlinePlayer.getUniqueId()).get(type.getID()).setLevel(level);
                        playerSender.sendMessage(STANDARD_COLOR + "You have set " + offlinePlayer.getName() + "'s " +  type.getName() + " level to " + level + ".");
                        break;
                    default:
                        break;
                }

            } else {
                playerSender.sendMessage(ERROR_COLOR + "Did you mean " + COMMAND_COLOR + "/balance" + ERROR_COLOR + "?");
            }
        }
        return true;
    }
}
