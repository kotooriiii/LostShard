package com.github.kotooriiii.commands;

import com.github.kotooriiii.sorcery.scrolls.Scroll;
import com.github.kotooriiii.sorcery.spells.Spell;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class ScrollCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player))
            return false;
        if(!(cmd.getName().equalsIgnoreCase("scroll")))
            return false;
        if(!sender.hasPermission(STAFF_PERMISSION))
        {
            sender.sendMessage(ChatColor.RED + "Next map mayb" + ChatColor.MAGIC + "e " + ChatColor.RESET + "" + ChatColor.RED + "?");
            return false;
        }

        Player player = (Player) sender;

        if(args.length == 0)
        {

            sendPage(1, player);

        } else {

            boolean exists = false;
            Spell spell = null;
            for(Spell iteratingSpell : Spell.getScrollableSpells())
            {
                for(String name : iteratingSpell.getNames())
                {
                    if(name.equalsIgnoreCase(args[0]))
                    {
                        exists = true;
                        spell = iteratingSpell;
                        break;
                    }
                }
            }

            if(!exists)
            {
                player.sendMessage(ERROR_COLOR + "The scroll does not exist.");
                return false;
            }

            player.getInventory().addItem(new Scroll(spell).createItem());

        }

        return false;
    }

    public void sendPage(int page, Player playerSender) {
        UUID playerUUID = playerSender.getUniqueId();

        final int amtOfSpellsPerPage = 5;

        Spell[] castableSpells = Spell.getScrollableSpells();

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

            playerSender.sendMessage(ChatColor.GOLD + "/scroll " + castableSpells[i].getName());

            spellTypeCounter++;
        }


    }
}
