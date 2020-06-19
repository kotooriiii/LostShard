package com.github.kotooriiii.bank.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.npc.type.banker.BankerNPC;
import com.github.kotooriiii.npc.type.banker.BankerTrait;
import com.github.kotooriiii.ranks.RankPlayer;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class ChestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("chest")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    final Location playerLocation = playerSender.getLocation();
                    NPC bankerNPC = BankerNPC.getNearestBanker(playerLocation);
                    BankerTrait bankerTrait = bankerNPC.getTrait(BankerTrait.class);
                    if (bankerNPC== null || !bankerTrait.isSocialDistance(playerLocation)) {
                        playerSender.sendMessage(ERROR_COLOR + "No banker nearby.");
                        return true;
                    }

                    RankPlayer rankPlayer = RankPlayer.wrap(playerUUID);
                    //get rank player
                    int rankSize = rankPlayer.getRankType().getBankInventorySize();

                    Inventory inventory = LostShardPlugin.getBankManager().wrap(playerUUID).getInventory();

                    ItemStack[] itemStacks = inventory.getContents();
                    inventory = Bukkit.createInventory(playerSender, rankSize, Bank.NAME);
                    for(int i = 0; i < itemStacks.length ; i++)
                    {
                        if(itemStacks[i] != null)
                            inventory.addItem(itemStacks[i]);

                    }
                    playerSender.openInventory(inventory);

                }
            }
        }
        return true;
    }
}
