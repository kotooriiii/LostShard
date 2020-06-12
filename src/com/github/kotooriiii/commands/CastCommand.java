package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

import static com.github.kotooriiii.data.Maps.*;

public class CastCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            if (cmd.getName().equalsIgnoreCase("cast")) {

                if (args.length == 0) {
                    playerSender.performCommand("cast 1");
                    return false;
                } else if (args.length >= 1) {

                    String name = HelperMethods.stringBuilder(args, 0, " ");

                    String numberString = args[0];
                    if (NumberUtils.isNumber(numberString) && !numberString.contains(".")) {

                        int page = Integer.parseInt(numberString);

                        if (page <= 0) {
                            playerSender.sendMessage(ERROR_COLOR + "You must use a positive integer for a page.");
                            return false;
                        }
                        sendPage(page, playerSender);
                        return true;
                    }

                    if(args[0].equalsIgnoreCase("help"))
                    {
                        playerSender.performCommand("cast 1");
                        return false;
                    }




                    SpellType type = SpellType.matchSpellType(name.toLowerCase());
                    if (type != null) {
                        Spell spell = Spell.of(type);
                        if (spell != null) {
                            if (Spell.of(type).isCastable()) {
                                spell.cast(playerSender);
                                return true;
                            }
                        }
                    }


                    playerSender.sendMessage(ERROR_COLOR + "The spell does not exist or you did not enter a positive integer for a page.");
                    return false;

                }

            }
        }
        return true;
    }

    public void sendPage(int page, Player playerSender) {
        UUID playerUUID = playerSender.getUniqueId();

        final int amtOfSpellsPerPage = 5;

        Spell[] castableSpells = Spell.getCastableSpells();

        int size = castableSpells.length;



        int pages = (int) Math.ceil((double) size / amtOfSpellsPerPage);
        if (pages == 0)
            pages = 1;
        int pageCounter = page;
        int spellTypeCounter = 0;


        if (page > pages) {
            playerSender.sendMessage(ERROR_COLOR + "There are not that many spells.");
            return;
        }

        playerSender.sendMessage(ChatColor.GOLD + "-Cast Help-");

        playerSender.sendMessage(ChatColor.GOLD + "Pg " + pageCounter + " of " + pages);

        for (int i = (page - 1) * amtOfSpellsPerPage; i < size; i++) {
            if (spellTypeCounter == amtOfSpellsPerPage) {
                return;
            }

            if (castableSpells == null)
                break;

            playerSender.sendMessage(ChatColor.GOLD + "/cast " + castableSpells[i].getName());

            spellTypeCounter++;
        }


    }


}
