package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.guards.ShardBanker;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

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
                    playerSender.sendMessage(ERROR_COLOR + "You must request an amount you'd like to withdraw. Example: " + COMMAND_COLOR + "/withdraw (amount)" + ERROR_COLOR + ".");


                } else if (args.length == 1) {
                    if (!NumberUtils.isNumber(args[0])) {
                        playerSender.sendMessage(ERROR_COLOR + "The provided amount is not a number.");
                        return true;
                    }

                    if(args[0].contains("."))
                    {
                        playerSender.sendMessage(ERROR_COLOR + "You can only withdraw full, intact, unbroken gold ingots. We assure you quality service!");
                        return true;
                    }

                    double withdraw = Double.parseDouble(args[0]);
                    DecimalFormat df = new DecimalFormat("#.##");
                    withdraw = Double.valueOf(df.format(withdraw));
                    final Location playerLocation = playerSender.getLocation();
                    ShardBanker banker = ShardBanker.getNearestBanker(playerLocation);
                    if (banker == null || !banker.isSocialDistance(playerLocation)) {
                        playerSender.sendMessage(ERROR_COLOR + "No banker nearby!!!");
                        return true;
                    }

                    Bank bank = Bank.getBanks().get(playerUUID);
                    double currency = Bank.getBanks().get(playerUUID).getCurrency();
                    double leftover = currency - withdraw;
                    leftover = Double.valueOf(df.format(leftover));

                    if (leftover < 0) {
                        playerSender.sendMessage(ERROR_COLOR + "You don't have that much money in your bank! We don't offer credit cards either.");
                        return true;
                    }

                    bank.setCurrency(leftover);
                    FileManager.write(bank);
                    playerSender.sendMessage(STANDARD_COLOR + "You've withdrawn " + MONEY_COLOR + df.format(withdraw) +STANDARD_COLOR + ". You have " + MONEY_COLOR + df.format(leftover) + STANDARD_COLOR + " left in your bank.");

                    int ingotsNum = (int) Math.floor(withdraw);
                    //int nuggetsNum = (int) withdraw%100;
                    HashMap<Integer, ItemStack> itemStacks = playerSender.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, ingotsNum));
                    int amountDropped=0;
                    for(Integer integer : itemStacks.keySet())
                    {
                        ItemStack itemStack = itemStacks.get(integer);
                        amountDropped += itemStack.getAmount();
                        playerLocation.getWorld().dropItem(playerLocation, itemStack);
                    }

                    if(amountDropped>0)
                    {
                        playerSender.sendMessage(STANDARD_COLOR + "You did not have enough inventory space for your withdrawal." + MONEY_COLOR + amountDropped + STANDARD_COLOR + " gold ingots been dropped on the ground.");

                    }


                } else {
                    playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments. Did you mean " + COMMAND_COLOR + " /withdraw (amount)" + ERROR_COLOR + "?");
                }
            }
        }
        return true;
    }
}
