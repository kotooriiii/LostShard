package com.github.kotooriiii.bank.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.bank.events.BankDepositEvent;
import com.github.kotooriiii.npc.type.banker.BankerNPC;
import com.github.kotooriiii.npc.type.banker.BankerTrait;
import com.github.kotooriiii.plots.privacy.PlotPrivacy;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import net.citizensnpcs.api.npc.NPC;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
                    playerSender.sendMessage(ChatColor.RED + "You must request an amount you'd like to deposit. Example: "+ "/deposit (amount)"  + ".");


                } else if (args.length == 1) {
                    if (!NumberUtils.isNumber(args[0])) {
                        playerSender.sendMessage(ChatColor.RED + "You can only deposit positive integers into your bank account.");
                        return true;
                    }

                    if(args[0].contains("."))
                    {
                        playerSender.sendMessage(ChatColor.RED + "You can only deposit positive integers into your bank account.");
                        return true;
                    }

                    double deposit = Double.parseDouble(args[0]);

                    if (deposit <= 0) {
                        playerSender.sendMessage(ChatColor.RED + "You can only deposit positive integers into your bank account.");
                        return true;
                    }

                    DecimalFormat df = new DecimalFormat("#.##");
                    deposit = Double.valueOf(df.format(deposit));
                    final Location playerLocation = playerSender.getLocation();
                    NPC bankerNPC = BankerNPC.getNearestBanker(playerLocation);
                    BankerTrait bankerTrait = bankerNPC.getTrait(BankerTrait.class);
                    if (bankerNPC== null || !bankerTrait.isSocialDistance(playerLocation)) {
                        playerSender.sendMessage(ERROR_COLOR + "No banker nearby.");
                        return true;
                    }

                    if (!bankerTrait.isStaffBanker() && bankerTrait.getPlot() instanceof PlayerPlot && !((PlayerPlot) bankerTrait.getPlot()).hasPermissionToUse(playerUUID))
                    {
                        playerSender.sendMessage(ERROR_COLOR + "The plot is private. The bankers will not help you with any transaction.");
                        return false;
                    }


                    Bank bank = LostShardPlugin.getBankManager().wrap(playerUUID);
                    double currency = bank.getCurrency();
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
                        playerSender.sendMessage(ChatColor.RED + "You tried to deposit " + Double.valueOf(df.format(deposit)) + " gold, but you only have " +  Double.valueOf(df.format(counterMoney)) + ".");

                    } else {
                        for (Map.Entry entry : hashmap.entrySet()) {
                            int key = (Integer) entry.getKey();
                            int value = (Integer) entry.getValue();
                            if (value == 0)
                                currentInventory.setItem(key, null);
                            else
                                currentInventory.setItem(key, new ItemStack(Material.GOLD_INGOT, value));
                        }
                        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(new BankDepositEvent(playerSender, deposit));
                        bank.setCurrency(leftover);
                        playerSender.sendMessage(ChatColor.GRAY + "You have deposited " + df.format(deposit) + " gold into your bank account.");
                    }
                } else {
                    playerSender.sendMessage(ChatColor.RED + "You provided too many arguments. Did you mean " + "/deposit (amount)"  + "?");
                }
            }
        }
        return true;
    }
}
