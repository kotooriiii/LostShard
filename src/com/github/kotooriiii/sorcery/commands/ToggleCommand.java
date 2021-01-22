package com.github.kotooriiii.sorcery.commands;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellToggleable;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class ToggleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {


        if(!(sender instanceof Player))
        {
            sender.sendMessage("You must be a player");
            return false;
        }

        if(!cmd.getName().equalsIgnoreCase("toggle"))
            return false;

        if(args.length == 0)
        {
            sender.sendMessage(ERROR_COLOR  + "Type /toggle <spellName> to toggle a toggleable spell off.");
            return true;
        }

        boolean exists = false;
        Spell spell = null;

        String spellName = HelperMethods.stringBuilder(args, 0, " ");
        final Spell[] toggleableSpells = Spell.getToggleableSpells();
        for(Spell spellToggleable : toggleableSpells)
        {
            for(String name : spellToggleable.getNames())
            {
                if(name.equalsIgnoreCase(spellName))
                {
                    spell = spellToggleable;
                    exists = true;
                    break;
                }
            }
        }




        if(!exists)
        {


            SpellType type = SpellType.matchSpellType(spellName.toLowerCase());
            if (type != null) {
                Spell attemptOtherSpell = Spell.of(type);
                if (attemptOtherSpell != null) {

                    sender.sendMessage(ERROR_COLOR + "The spell \"" + spellName + "\" is not toggleable.");
                    return false;
                }
            }

            sender.sendMessage(ERROR_COLOR + "The toggleable spell \"" + spellName + "\" was not found.");
            return false;
        }

        boolean isDraining = false;
        for (Map.Entry<SpellToggleable, HashSet<UUID>> entry : Spell.getManaDrainMap().entrySet()) {
            final SpellToggleable spellKey = entry.getKey();
            final HashSet<UUID> set = entry.getValue();
            final Iterator<UUID> iterator = set.iterator();
            while (iterator.hasNext()) {

                UUID uuid = iterator.next();

                if(!uuid.equals(((Player) sender).getUniqueId()))
                    continue;

                spellKey.stopManaDrain(uuid);
                iterator.remove();
                isDraining = true;
                break;
            }
        }

        if(isDraining)
        {
            sender.sendMessage(ChatColor.GOLD  + "You toggled the spell \"" + spell.getName() + "\" off.");

        } else
        {
            sender.sendMessage(ERROR_COLOR + "The spell \"" + spell.getName() + "\" is not toggled on.");

        }




        return false;
    }
}
