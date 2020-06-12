package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.npc.type.banker.BankerNPC;
import com.github.kotooriiii.npc.type.banker.BankerTrait;
import net.citizensnpcs.api.npc.NPC;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class WithdrawCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("withdraw")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    playerSender.sendMessage(ChatColor.RED + "You must request an amount you'd like to withdraw. Example: " + "/withdraw (amount)" + ".");


                } else if (args.length == 1) {
                    if (!NumberUtils.isNumber(args[0])) {
                        playerSender.sendMessage(ChatColor.RED + "You can only deposit positive integers into your bank account.");
                        return true;
                    }

                    if (args[0].contains(".")) {
                        playerSender.sendMessage(ChatColor.RED + "You can only deposit positive integers into your bank account.");
                        return true;
                    }

                    double withdraw = Double.parseDouble(args[0]);
                    DecimalFormat df = new DecimalFormat("#.##");
                    withdraw = Double.valueOf(df.format(withdraw));
                    final Location playerLocation = playerSender.getLocation();
                    NPC bankerNPC = BankerNPC.getNearestBanker(playerLocation);
                    BankerTrait bankerTrait = bankerNPC.getTrait(BankerTrait.class);
                    if (bankerNPC== null || !bankerTrait.isSocialDistance(playerLocation)) {
                        playerSender.sendMessage(ERROR_COLOR + "No banker nearby.");
                        return true;
                    }

                    Bank bank = Bank.getBanks().get(playerUUID);
                    double currency = Bank.getBanks().get(playerUUID).getCurrency();
                    double leftover = currency - withdraw;
                    leftover = Double.valueOf(df.format(leftover));

                    if (leftover < 0) {
                        playerSender.sendMessage(ChatColor.RED + "You tried to withdraw " + Double.valueOf(df.format(withdraw)) + " gold, but your bank only has " + Double.valueOf(df.format(currency)) + ".");
                        return true;
                    }

                    int ingotsNum = (int) Math.floor(withdraw);
                    HashMap<Integer, ItemStack> itemStacks = playerSender.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, ingotsNum));
                    int amountDropped = 0;
                    for (Integer integer : itemStacks.keySet()) {
                        ItemStack itemStack = itemStacks.get(integer);
                        amountDropped += itemStack.getAmount();
                    }

                    if (amountDropped > 0) {
                        playerSender.sendMessage(ChatColor.RED + "Your inventory is full. " + amountDropped + " gold was kept in your bank account.");
                        bank.setCurrency(leftover + amountDropped);
                    } else {
                        playerSender.sendMessage(ChatColor.GRAY + "You have withdrawn " + df.format(withdraw) + " gold from your bank account.");
                        bank.setCurrency(leftover);
                    }
                    FileManager.write(bank);

                } else {
                    playerSender.sendMessage(ChatColor.RED + "You provided too many arguments. Did you mean " + " /withdraw (amount)" + "?");
                }
            }
        }
        return true;
    }
}
