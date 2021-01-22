package com.github.kotooriiii.commands;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.type.circle1.LightSpell;
import com.github.kotooriiii.sorcery.spells.type.circle7.RadiateSpell;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.COMMAND_COLOR;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class RestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("rest")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    if(Stat.getRestingPlayers().contains(playerUUID))
                    {
                        playerSender.sendMessage(ChatColor.GOLD + "You are already resting...");
                        return true;
                    }
                    playerSender.sendMessage(ChatColor.GOLD + "You begin resting.");
                    Stat.getRestingPlayers().add(playerUUID);

                    for(Spell spell : Spell.getToggleableSpells())
                    {
                        final HashSet<UUID> uuids = Spell.getManaDrainMap().get(spell);
                        if(uuids != null)
                        {
                            if(uuids.contains(((Player) sender).getUniqueId()))
                            {
                                uuids.remove(((Player) sender).getUniqueId());
                                sender.sendMessage(ChatColor.GOLD + "You toggled the spell \"" + spell.getName() + "\" off.");
                                RadiateSpell.getInstance().stopManaDrain(((Player) sender).getUniqueId());
                                break;
                            }
                        }
                    }









                } else {
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean " + COMMAND_COLOR + "/rest" + ERROR_COLOR + "?");
                }
            }
        }
        return true;
    }
}
