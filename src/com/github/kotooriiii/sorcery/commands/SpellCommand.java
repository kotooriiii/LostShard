package com.github.kotooriiii.sorcery.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.util.HelperMethods;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class SpellCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            if (cmd.getName().equalsIgnoreCase("spell")) {

                if (args.length == 0) {
                    playerSender.performCommand("spell 1");
                    return false;
                } else if (args.length >= 1) {

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

                    if (args[0].equalsIgnoreCase("help")) {
                        playerSender.performCommand("spell 1");
                        return false;
                    }

                    playerSender.sendMessage(ERROR_COLOR + "Have more questions? Type: /spellbook");
                    return false;
                }

            }
        }
        return true;
    }

    public void sendPage(int page, Player playerSender) {
        UUID playerUUID = playerSender.getUniqueId();

        final int amtOfSpellsPerPage = 5;


        Spell[] castableSpells = LostShardPlugin.getSorceryManager().wrap(playerUUID).getSpells();

        int size = castableSpells.length;


        int pages = (int) Math.ceil((double) size / amtOfSpellsPerPage);
        if (pages == 0)
            pages = 1;
        int pageCounter = page;
        int spellTypeCounter = 0;


        if (page > pages) {
            playerSender.sendMessage(ERROR_COLOR + "This page is empty.");
            return;
        }

        playerSender.sendMessage(ChatColor.GOLD + "-Spells Help-");

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
