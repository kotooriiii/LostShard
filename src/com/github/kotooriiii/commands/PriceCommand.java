package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.bank.Sale;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.util.HelperMethods;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.github.kotooriiii.data.Maps.*;

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
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean " + COMMAND_COLOR + "/price (amount) (item)" + ERROR_COLOR + "?");
                    return false;
                }


                //Check first argument
                if (!NumberUtils.isNumber(args[0])) {
                    playerSender.sendMessage(ERROR_COLOR + "The provided amount you'd like to price check is not a number.");
                    return true;
                }

                if (args[0].contains(".")) {
                    playerSender.sendMessage(ERROR_COLOR + "You can only price check full, intact, unbroken items.");
                    return true;
                }

                //Check second argument
                Material material = Material.matchMaterial(args[1].replace(" ", "_").toUpperCase());
                if (material == null) {
                    playerSender.sendMessage(ERROR_COLOR + "The item you are looking for doesn't exist.");
                    Material[] materials = HelperMethods.getNearestMaterials(args[1], 3);
                    if (materials == null || materials.length == 0) {
                        playerSender.sendMessage(ERROR_COLOR + "Could not find a material close to your search.");
                        return true;
                    }
                    String[] materialNames = new String[materials.length];
                    for (int i = 0; i < materialNames.length; i++) {
                        if(materials[i] == null)
                            break;
                        materialNames[i] = materials[i].getKey().getKey().toLowerCase();
                    }
                    String builder = HelperMethods.stringBuilder(materialNames, 0, ", ", ", or ");
                    playerSender.sendMessage(ERROR_COLOR + "Did you possibly mean to price check one of these items?: " + builder);
                    return true;
                }

                //User input restrictions passed
                int amount = Integer.parseInt(args[0]);

                List<Sale> sortedSales = new ArrayList<>();
                for (Sale sale : Sale.getSales()) {
                    if (sale.getMaterial().equals(material)) {
                        sortedSales.add(sale);
                    }
                }

                //The material we are searching for is out of stock.
                if (sortedSales.isEmpty()) {
                    playerSender.sendMessage(ERROR_COLOR + "Nobody is currently selling the item, \"" + material.getKey().getKey().toLowerCase().replace("_", " ") + "\".");
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

                saleLoop:
                for (Sale sale : sortedSales) { //Iterate through all the sales that are sorted from cheapest material to expensive material.

                    //We will be looping once every time so this means that we are adding one more item to the counter each time.

                    //The temporary amount we will hold is one more. The initial condition is starting with one purchase.
                    int tempTotalAmountCounter = amountCounter + 1;
                    //The temporary amount we will hold is the starting price. Initial condition is sale price for individual item.
                    double tempTotalPriceCounter = priceCounter + sale.getPrice();

                    //The item being tracked. If null entire time, just don't add it. We don't need to give the player an empty item.
                    ItemStack itemPurchased = null;

                    itemLoop:
                    for (int i = 0; i < sale.getAmount(); i++) {

                        //We are getting more than we need, stop!!
                        //OR
                        //We don't have money to keep going. STOP!!
                        if (tempTotalAmountCounter > amount)
                            break itemLoop;

                        //Less than or equal the limit, we are able to make the purchase.

                        //In theory, we 'purchased' it. Now promise the item.
                        if (itemPurchased == null)
                            itemPurchased = new ItemStack(sale.getMaterial(), 1); //Only buying one!
                        else
                            itemPurchased.setAmount(itemPurchased.getAmount() + 1);

                        //

                        tempTotalAmountCounter = tempTotalAmountCounter + 1; //Incrementor
                        tempTotalPriceCounter = priceCounter + (sale.getPrice() * (i + 2)); //Incrementor
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
                    playerSender.sendMessage(ERROR_COLOR + "We couldn't find the price of " + amount + " " + material.getKey().getKey().toLowerCase().replace("_", " " ) +
                            ". However, we were able to find that " + amountCounter +  " " + material.getKey().getKey().toLowerCase().replace("_", " " )  + " is on sale for " + priceCounter + ".");
                }

            }
        }
        return true;
    }

}
