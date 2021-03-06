package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.skills.Skill;
import com.github.kotooriiii.skills.SkillBuild;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.skills.SkillType;
import com.github.kotooriiii.util.HelperMethods;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
                    showSkills(playerSender);
                    return false;
                }

                switch (args[0].toLowerCase()) {

                    case "unlock": //skills | lock (skillName)
                        if (args.length != 2) {
                            playerSender.sendMessage(STANDARD_COLOR + "To lock a skill do: /skills unlock (skill)");
                            return false;
                        }

                        if (!SkillType.isSkill(args[1].toUpperCase())) {
                            SkillType[] skillTypeArr = SkillType.values();
                            String[] arr = new String[skillTypeArr.length];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = skillTypeArr[i].getName();
                            }
                            String[] closestArr = HelperMethods.getNearest(args[1], arr, 3);
                            String items = HelperMethods.stringBuilder(closestArr, 0, ", ", ", or ", " or ");

                            if (items.isEmpty())
                                playerSender.sendMessage(ERROR_COLOR + "The skill \"" + args[1] + "\" is invalid.");

                            else
                                playerSender.sendMessage(ERROR_COLOR + "The skill \"" + args[1] + "\" is invalid. Did you mean: " + items + "?");
                            return false;
                        }

                        SkillType skillTypeUnlock = SkillType.valueOf(args[1].toUpperCase());
                        Skill skillUnlock = LostShardPlugin.getSkillManager().getSkillPlayer(playerUUID).getActiveBuild().getSkill(skillTypeUnlock);

                        if (!skillUnlock.isLocked()) {
                            playerSender.sendMessage(ERROR_COLOR + "Your skill is already unlocked. To lock a skill do: /skills lock (skill)");
                            return false;
                        }

                        skillUnlock.setLocked(false);
                        playerSender.sendMessage(STANDARD_COLOR + "You have unlocked " + skillUnlock.getType().getName() + ".");

                        break;
                    case "lock": //skills | lock (skillName)
                        if (args.length != 2) {
                            playerSender.sendMessage(STANDARD_COLOR + "To lock a skill do: /skills lock (skill)");
                            return false;
                        }

                        if (!SkillType.isSkill(args[1].toUpperCase())) {
                            SkillType[] skillTypeArr = SkillType.values();
                            String[] arr = new String[skillTypeArr.length];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = skillTypeArr[i].getName();
                            }
                            String[] closestArr = HelperMethods.getNearest(args[1], arr, 3);
                            String items = HelperMethods.stringBuilder(closestArr, 0, ", ", ", or ", " or ");

                            if (items.isEmpty())
                                playerSender.sendMessage(ERROR_COLOR + "The skill \"" + args[1] + "\" is invalid.");

                            else
                                playerSender.sendMessage(ERROR_COLOR + "The skill \"" + args[1] + "\" is invalid. Did you mean: " + items + "?");
                            return false;
                        }

                        SkillType skillTypeLock = SkillType.valueOf(args[1].toUpperCase());
                        Skill skillLock = LostShardPlugin.getSkillManager().getSkillPlayer(playerUUID).getActiveBuild().getSkill(skillTypeLock);

                        if (skillLock.isLocked()) {
                            playerSender.sendMessage(ERROR_COLOR + "Your skill is already locked. To unlock a skill do: /skills unlock (skill)");
                            return false;
                        }

                        skillLock.setLocked(true);
                        playerSender.sendMessage(STANDARD_COLOR + "You have locked " + skillLock.getType().getName() + ".");

                        break;

                    case "reduce": //skills | reduce (skillName) (amount)
                        if (args.length != 3) {
                            playerSender.sendMessage(STANDARD_COLOR + "To reduce a skill's level do: /skills reduce (skill) (amount to reduce)");
                            return false;
                        }


                        if (!SkillType.isSkill(args[1].toUpperCase())) {
                            SkillType[] skillTypeArr = SkillType.values();
                            String[] arr = new String[skillTypeArr.length];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = skillTypeArr[i].getName();
                            }
                            String[] closestArr = HelperMethods.getNearest(args[1], arr, 3);
                            String items = HelperMethods.stringBuilder(closestArr, 0, ", ", ", or ", " or ");

                            if (items.isEmpty())
                                playerSender.sendMessage(ERROR_COLOR + "The skill \"" + args[1] + "\" is invalid.");

                            else
                                playerSender.sendMessage(ERROR_COLOR + "The skill \"" + args[1] + "\" is invalid. Did you mean: " + items + "?");
                            return false;
                        }

                        SkillType skillTypeReduced = SkillType.valueOf(args[1].toUpperCase());

                        if (!NumberUtils.isNumber(args[2])) {
                            playerSender.sendMessage(ERROR_COLOR + "You must choose a number to reduce the skill's level.");
                            return false;
                        }
                        float amount = new BigDecimal(NumberUtils.createFloat(args[2]).floatValue()).setScale(1, RoundingMode.HALF_UP).floatValue();

                        if (!(0 <= amount && amount <= 100)) {
                            playerSender.sendMessage(ERROR_COLOR + "You must choose a number between 0 to 100.");
                            return false;
                        }


                        Skill reducedSkill = LostShardPlugin.getSkillManager().getSkillPlayer(playerUUID).getActiveBuild().getSkill(skillTypeReduced);

                        float newLevel = new BigDecimal(new BigDecimal(reducedSkill.getLevel()).setScale(1, RoundingMode.HALF_UP).floatValue() - amount).setScale(1, RoundingMode.HALF_UP).floatValue();


                        if (newLevel < 0) {
                            newLevel = 0.0f;
                        }

                        /*
                        Valid skill type
                        Must be a number
                        number between 0 to 100 inclusive
                        new skill cannot be below 0
                         */
                        reducedSkill.setLevel(newLevel);
                        playerSender.sendMessage(STANDARD_COLOR + "You have reduced " + reducedSkill.getType().getName() + "'s level to " + new BigDecimal(reducedSkill.getLevel()).setScale(1, RoundingMode.HALF_UP) + ".");

                        break;
                    case "show":
                        showSkills(playerSender);
                        break;
                    case "staff": //skills | staff <player> <skill> <level>

                        if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                            playerSender.sendMessage(ERROR_COLOR + "You do not have staff permission to access this page.");
                            return false;
                        }

                        if (args.length != 4) {
                            playerSender.sendMessage(STANDARD_COLOR + "To set the level of a player do: /skills staff (username) (skill) (level)");
                            return false;
                        }

                        String playerNameString = args[1];
                        String skillString = args[2];
                        String levelString = args[3];

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerNameString);
                        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                            playerSender.sendMessage(ERROR_COLOR + "The player you have listed does not have a Skills account (does the player exist?).");
                            return false;
                        }

                        if (!SkillType.isSkill(skillString)) {
                            playerSender.sendMessage(ERROR_COLOR + "You must set a valid skill type.");
                            return false;
                        }

                        SkillType type = SkillType.valueOf(skillString.toUpperCase());

                        if (!NumberUtils.isNumber(levelString)) {
                            playerSender.sendMessage(ERROR_COLOR + "You must set a number based level.");
                            return false;
                        }

                        float level = Float.valueOf(levelString);
                        SkillPlayer skillPlayer = LostShardPlugin.getSkillManager().getSkillPlayer(offlinePlayer.getUniqueId());
                        SkillBuild build = skillPlayer.getActiveBuild();
                        Skill skill = null;
                        for (Skill iskill : build.getSkills()) {
                            if (iskill.getType().equals(type))
                                skill = iskill;
                        }
                        if (skill == null)
                            return false;
                        skill.setLevel(level);
                        playerSender.sendMessage(STANDARD_COLOR + "You have set " + offlinePlayer.getName() + "'s " + type.getName() + " level to " + level + ".");
                        break;
                    default:
                        sendHelp(playerSender);
                        break;
                }

            } else {
                playerSender.sendMessage(ERROR_COLOR + "Did you mean " + COMMAND_COLOR + "/balance" + ERROR_COLOR + "?");
            }
        }
        return true;
    }

    public void showSkills(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "-" + playerSender.getName() + "'s Skills-");

        SkillPlayer skillPlayer = LostShardPlugin.getSkillManager().getSkillPlayer(playerSender.getUniqueId());
        float skillNum = 0;
        float maxSkillNum = skillPlayer.getActiveBuild().getMaxPoints();

        for (Skill skill : skillPlayer.getActiveBuild().getSkills()) {
            skillNum += skill.getLevel();
        }


        playerSender.sendMessage(ChatColor.YELLOW + "You currently have " + new BigDecimal(skillNum).setScale(1, RoundingMode.HALF_UP).toString() + "/" + new BigDecimal(maxSkillNum).setScale(1, RoundingMode.HALF_UP).toString() + " skill points.");

        String isLocked = "";
        for (Skill skill : skillPlayer.getActiveBuild().getSkills()) {
            if (skill.isLocked())
                isLocked = " (L)";
            else isLocked = "";
            playerSender.sendMessage(ChatColor.YELLOW + skill.getType().getName() + isLocked + ": " + ChatColor.WHITE + new BigDecimal(skill.getLevel()).setScale(1, RoundingMode.HALF_UP).toString());

        }
    }

    public void sendHelp(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "------Skills Help------");
        playerSender.sendMessage(COMMAND_COLOR + "/skills show");
        playerSender.sendMessage(COMMAND_COLOR + "/skills reduce " + ChatColor.YELLOW + "(skill name) (amount to reduce)");
        playerSender.sendMessage(COMMAND_COLOR + "/skills lock " + ChatColor.YELLOW + "(skill name)");
        playerSender.sendMessage(COMMAND_COLOR + "/skills unlock " + ChatColor.YELLOW + "(skill name)");
    }
}
