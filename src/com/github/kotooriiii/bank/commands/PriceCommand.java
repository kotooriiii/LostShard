package com.github.kotooriiii.bank.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Sale;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.*;

public class PriceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();

            //If the command is balance
            if (cmd.getName().equalsIgnoreCase("price")) {
                //No arguments regarding this command
                if (args.length != 2) {
                    playerSender.sendMessage(ChatColor.RED + "Did you mean "  + "/price (amount) (item)"  + "?");
                    return false;
                }


                //Check first argument
                if (!NumberUtils.isNumber(args[0])) {
                    playerSender.sendMessage(ChatColor.RED + "You must use positive integers to price check items.");
                    return true;
                }

                if (args[0].contains(".")) {
                    playerSender.sendMessage(ChatColor.RED + "You must use positive integers to price check items.");
                    return true;
                }

                //Check second argument
                ItemStack ingredient = SellCommand.getItem(playerSender, args[1].substring(0,1).toUpperCase() + args[1].substring(1).toLowerCase());
                if(ingredient==null)
                    return false;

                //User input restrictions passed
                int amount = Integer.parseInt(args[0]);
                if(amount<=0)
                {
                    playerSender.sendMessage(ChatColor.RED + "You must use positive integers to price check items.");
                    return false;
                }

                List<Sale> sortedSales = new ArrayList<>();
                for (Sale sale : LostShardPlugin.getSaleManager().getSales()) {
                    if (sale.getItemStack().getType().equals(ingredient.getType())) {
                        if (ingredient.getItemMeta() instanceof PotionMeta) {
                            if (!(sale.getItemStack().getItemMeta() instanceof PotionMeta))
                                continue;

                            PotionMeta saleMeta = (PotionMeta) sale.getItemStack().getItemMeta();
                            if (!((PotionMeta) saleMeta).getBasePotionData().equals(((PotionMeta) ingredient.getItemMeta()).getBasePotionData()))
                                continue;
                        }
                        sortedSales.add(sale);
                    }
                }
                String name = ingredient.getType().name().replace("_", " ").toLowerCase() + " ";
                name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
                if(ingredient.getItemMeta() instanceof PotionMeta) {
                    name = ((PotionMeta) ingredient.getItemMeta()).getBasePotionData().getType().name();
                    name = name.substring(0,1).toUpperCase() + name.substring(1).toUpperCase() + " ";
                    if(((PotionMeta) ingredient.getItemMeta()).getBasePotionData().isExtended())
                        name += "extended ";
                    else if(((PotionMeta) ingredient.getItemMeta()).getBasePotionData().isUpgraded())
                        name += "2 Potion ";
                    else
                        name+= "Potion ";
                }

                //The material we are searching for is out of stock.
                if (sortedSales.isEmpty()) {
                    playerSender.sendMessage(ChatColor.RED + "The economy has no " + name + "left.");
                    return false;
                }

                Collections.sort(sortedSales);

                //The total amount of items purchased and the price total.
                int amountCounter = 0;
                double priceCounter = 0;

                //The items purchased in items
                ArrayList<ItemStack> itemsPurchased = new ArrayList<>();
                //The map of sales. The key representing the sale and the amount left. Remember! If the integer amount is 0 than make sure to remove from the arraylist in Sale manager.
                HashMap<Sale, Integer> saleMap = new HashMap<>();

                for (Sale sale : sortedSales) { //Iterate through all the sales that are sorted from cheapest material to expensive material.

                    //We will be looping once every time so this means that we are adding one more item to the counter each time.

                    //The temporary amount we will hold is one more. The initial condition is starting with one purchase.
                    int tempTotalAmountCounter = amountCounter+ 1;
                    //The temporary amount we will hold is the starting price. Initial condition is sale price for individual item.
                    double tempTotalPriceCounter = priceCounter + sale.getPrice();

                    //The item being tracked. If null entire time, just don't add it. We don't need to give the player an empty item.
                    ItemStack itemPurchased = null;

                    itemLoop:
                    for (int i = 0; i < sale.getAmount(); i++) {

                        //We are getting more than we need, stop!!
                        //OR
                        //We don't have money to keep going. STOP!!
                        if (tempTotalAmountCounter > amount) {
                            tempTotalAmountCounter--;
                            tempTotalPriceCounter -= sale.getPrice();

                            break itemLoop;
                        }

                        //Less than or equal the limit, we are able to make the purchase.

                        //In theory, we 'purchased' it. Now promise the item.
                        if (itemPurchased == null)
                        {
                            itemPurchased = new ItemStack(sale.getItemStack().getType(), 1); //Only buying one!
                            if(ingredient.getItemMeta() instanceof PotionMeta)
                            {
                                itemPurchased.setItemMeta(ingredient.getItemMeta());
                            }
                        }
                        else
                            itemPurchased.setAmount(itemPurchased.getAmount() + 1);

                        if(i != sale.getAmount()-1) {
                            tempTotalAmountCounter = tempTotalAmountCounter + 1; //Incrementor
                            tempTotalPriceCounter = tempTotalPriceCounter + sale.getPrice(); //Incrementor
                        }
                    }

                    //If the item was purchased, then make sure we update the sale to show the correct item.
                    if (itemPurchased != null) {
                        itemsPurchased.add(itemPurchased); //Add to array of items purchased to give to player.
                        saleMap.put(sale, sale.getAmount() - itemPurchased.getAmount()); //For updating purposes after the loop ends.
                        priceCounter = tempTotalPriceCounter;
                        amountCounter = tempTotalAmountCounter;
                    } //The item was never purchased, this means that the person could not afford it or we've reached the maximum quantity.
                    else break;
                }

                //Not enough items but its okay.
                if (amount > amountCounter) {
                    playerSender.sendMessage(ChatColor.RED + "Only " + amountCounter + " " + name +
                            "remains in the economy.");
                    return false;
                }

                playerSender.sendMessage(ChatColor.GRAY + "" + amountCounter + " " + name + "costs " + priceCounter + " gold.");

            }
        }
        return true;
    }

}
