package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.guards.ShardBanker;
import net.minecraft.server.v1_15_R1.Packet;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class DepositCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("deposit")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    playerSender.sendMessage(ERROR_COLOR + "You must request an amount you'd like to deposit. Example: " + COMMAND_COLOR + "/deposit (amount)" + ERROR_COLOR + ".");


                } else if (args.length == 1) {
                    if (!NumberUtils.isNumber(args[0])) {
                        playerSender.sendMessage(ERROR_COLOR + "The provided amount is not a number.");
                        return true;
                    }

                    double deposit = Double.parseDouble(args[0]);
                    DecimalFormat df = new DecimalFormat("#.##");
                    deposit = Double.valueOf(df.format(deposit));
                    final Location playerLocation = playerSender.getLocation();
                    ShardBanker banker = ShardBanker.getNearestBanker(playerLocation);
                    if (banker == null || !banker.isSocialDistance(playerLocation)) {
                        playerSender.sendMessage(ERROR_COLOR + "No banker nearby!!!");
                        return true;
                    }

                    Bank bank = Bank.getBanks().get(playerUUID);
                    double currency = Bank.getBanks().get(playerUUID).getCurrency();
                    double leftover = currency + deposit;
                    leftover = Double.valueOf(df.format(leftover));

                    Inventory currentInventory = playerSender.getInventory();
                    ItemStack[] itemStacks = currentInventory.getContents();
                    int counterMoney = 0;
                    HashMap<Integer, Integer> hashmap = new HashMap<>();
                    for (int i = 0; i < itemStacks.length; i++) {
                        ItemStack itemStack = itemStacks[i];
                        if (itemStack == null || !itemStack.getType().equals(Material.GOLD_INGOT))
                            continue;
                        int iteratingMoney = itemStack.getAmount();
                        int tempTotal = iteratingMoney + counterMoney;
                        if (deposit >= tempTotal) {
                            counterMoney += iteratingMoney;
                            hashmap.put(new Integer(i), 0);
                        } else if (deposit < tempTotal) {
                            int tempLeftover = tempTotal - (int) deposit;
                            hashmap.put(new Integer(i), tempLeftover);
                            counterMoney = (int) deposit;
                            break;
                        }
                    }

                    if (counterMoney < deposit) {
                        playerSender.sendMessage(ERROR_COLOR + "You didn't have sufficient funds to deposit to your bank account. You currently have " + MONEY_COLOR +  Double.valueOf(df.format(counterMoney)) + ERROR_COLOR + " in your inventory.");

                    } else {
                        for (Map.Entry entry : hashmap.entrySet()) {
                            int key = (Integer) entry.getKey();
                            int value = (Integer) entry.getValue();
                            if (value == 0)
                                currentInventory.setItem(key, null);
                            else
                                currentInventory.setItem(key, new ItemStack(Material.GOLD_INGOT, value));
                        }
                        bank.setCurrency(leftover);
                        FileManager.write(bank);
                        playerSender.sendMessage(STANDARD_COLOR + "You've deposited " + MONEY_COLOR + deposit + STANDARD_COLOR + ". You have added " + MONEY_COLOR + leftover + STANDARD_COLOR + " to your bank account.");
                    }
                } else {
                    playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments. Did you mean " + COMMAND_COLOR + "/deposit (amount)" + ERROR_COLOR + "?");
                }
            }
        }
        return true;
    }
}
