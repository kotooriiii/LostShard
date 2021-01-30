package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.BindEvent;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import com.github.kotooriiii.sorcery.wands.Wand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class BindCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("bind")) {


                Stat stat = Stat.getStatMap().get(playerUUID);
                //No arguments regarding this command
                if (args.length == 0) {
                    playerSender.sendMessage(ERROR_COLOR + "You need to provide the name of a spell.");
                    return true;
                } else if (args.length >= 1) {

                    String namebuilder = HelperMethods.stringBuilder(args, 0, " ");
                    // Loop through all possible
                    boolean isFound = false;
                    Wand wand = null;
                    for (SpellType spellType : SpellType.oldMapValues()) {

                        // Check if wand is the one requested
                        for(String iname : spellType.getNames())
                        {
                            if(iname.equalsIgnoreCase(namebuilder))
                            {
                                // Create wand item
                                wand =  new Wand(Spell.of(spellType));
                                isFound = true;
                                break;
                            }
                        }
                    }

                    if(!isFound)
                    {
                        playerSender.sendMessage(ERROR_COLOR + "That spell does not exist.");
                        return false;
                    }

                    ItemStack inHand = playerSender.getInventory().getItemInMainHand();
                    if (inHand == null || inHand.getType() != Material.STICK) {
                        playerSender.sendMessage(ERROR_COLOR + "You need a stick in your main hand to bind a spell!");
                        return true;
                    }

                    BindEvent bindEvent = new BindEvent(playerSender, wand.getSpell());
                    LostShardPlugin.plugin.getServer().getPluginManager().callEvent(bindEvent);
                    if(bindEvent.isCancelled())
                        return false;

                    ItemStack wandItem = wand.createItem();
                    if(inHand.getAmount() > 1)
                    {
                        int leftover = inHand.getAmount()-1;
                        playerSender.getInventory().setItemInMainHand(wandItem);
                        HashMap<Integer,ItemStack> items = playerSender.getInventory().addItem(new ItemStack(Material.STICK, leftover));
                        for(ItemStack droppedItems : items.values())
                        {
                            playerSender.getWorld().dropItem(playerSender.getLocation(), droppedItems);
                            playerSender.sendMessage(STANDARD_COLOR + "You did not have enough space to store your other sticks. They have been dropped on the ground.");
                        }
                    } else {
                        playerSender.getInventory().setItemInMainHand(wandItem);
                    }


                }
            }
        }


        return true;
    }
}
