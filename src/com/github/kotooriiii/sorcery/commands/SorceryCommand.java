package com.github.kotooriiii.sorcery.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.Skill;
import com.github.kotooriiii.skills.SkillBuild;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.skills.SkillType;
import com.github.kotooriiii.sorcery.spells.SorceryPlayer;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.util.HelperMethods;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class SorceryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("sorcery")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    return false;
                }

                switch (args[0].toLowerCase()) {
                    case "unknown":
                        break;
                    case "staff": //skills | staff <player> <skill> <level>

                        if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                            playerSender.sendMessage(ERROR_COLOR + "You do not have staff permission to access this page.");
                            return false;
                        }

                        if (args.length < 3) {
                            playerSender.sendMessage(STANDARD_COLOR + "To remove/add a player spell do: /sorcery staff (username) (spellName)");
                            return false;
                        }

                        String playerNameString = args[1];
                        String spellNameString = HelperMethods.stringBuilder(args, 2, " ");

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerNameString);
                        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                            playerSender.sendMessage(ERROR_COLOR + "The player you have listed does not have a Sorcery account (does the player exist?).");
                            return false;
                        }

                        SpellType type = SpellType.matchSpellType(spellNameString.toLowerCase());
                        if (type == null)
                        {
                            playerSender.sendMessage(ERROR_COLOR + "The spell does not exist");
                            return false;
                        }


                        SorceryPlayer sorceryPlayer = LostShardPlugin.getSorceryManager().wrap(offlinePlayer.getUniqueId());

                        if (!sorceryPlayer.hasSpell(type)) {
                            sorceryPlayer.addSpell(type);
                            playerSender.sendMessage(STANDARD_COLOR + "You added " + ChatColor.DARK_PURPLE + type.getName() +  STANDARD_COLOR + " to " + offlinePlayer.getName() + "'s spellbook.");
                        } else {
                            sorceryPlayer.removeSpell(type);
                            playerSender.sendMessage(STANDARD_COLOR + "You removed " + ChatColor.DARK_PURPLE + type.getName() +  STANDARD_COLOR + " from " + offlinePlayer.getName() + "'s spellbook.");
                        }
                        break;
                    default:
                        break;
                }

            }
        }
        return true;
    }
}
