package com.github.kotooriiii.bank.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.bank.Sale;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class BuyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();

            //If the command is balance
            if (cmd.getName().equalsIgnoreCase("buy")) {
                //No arguments regarding this command
                if (args.length != 3) {
                    playerSender.sendMessage(ChatColor.RED + "Did you mean " + "/buy (amount) (item) (limit)" + "?");
                    return false;
                }

//                String possibleAmount = args[0];
//                String possibleItem = args[1];
//                String possibleLimit = args[2];

                //Check first argument
                if (!NumberUtils.isNumber(args[0])) {
                    playerSender.sendMessage(ChatColor.RED + "You must use positive integers to buy items.");
                    return true;
                }

                if (args[0].contains(".")) {
                    playerSender.sendMessage(ChatColor.RED + "You must use positive integers to buy items.");
                    return true;
                }

                //Check second argument
                ItemStack ingredient = SellCommand.getItem(playerSender, args[1].substring(0, 1).toUpperCase() + args[1].substring(1).toLowerCase());
                if (ingredient == null)
                    return false;

                //Check third argument
                if (!NumberUtils.isNumber(args[2])) {
                    playerSender.sendMessage(ChatColor.RED + "/buy (amount) (item) (limit)");
                    return true;
                }

                //User input restrictions passed
                int amount = Integer.parseInt(args[0]);
                if (amount <= 0) {
                    playerSender.sendMessage(ChatColor.RED + "You must use positive integers to buy items.");
                    return false;
                }
                double limit = new BigDecimal(args[2]).setScale(2, RoundingMode.HALF_UP).doubleValue();
                Bank buyerBank = LostShardPlugin.getBankManager().wrap(playerUUID);

                //The limit is higher than what you have in your bank
                if (limit > buyerBank.getCurrency()) {
                    playerSender.sendMessage(ChatColor.RED + "You don't have enough currency for that limit.");
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
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                if (ingredient.getItemMeta() instanceof PotionMeta) {
                    name = ((PotionMeta) ingredient.getItemMeta()).getBasePotionData().getType().name();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toUpperCase() + " ";
                    if (((PotionMeta) ingredient.getItemMeta()).getBasePotionData().isExtended())
                        name += "extended ";
                    else if (((PotionMeta) ingredient.getItemMeta()).getBasePotionData().isUpgraded())
                        name += "2 Potion ";
                    else
                        name += "Potion ";
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

                saleLoop:
                for (Sale sale : sortedSales) { //Iterate through all the sales that are sorted from cheapest material to expensive material.

                    //We will be looping once every time so this means that we are adding one more item to the counter each time.

                    //The temporary amount we will hold is one more. The initial condition is starting with one purchase.
                    int tempTotalAmountCounter = amountCounter + 1;
                    //The temporary amount we will hold is the starting price. Initial condition is sale price for individual item.
                    double tempTotalPriceCounter = priceCounter + sale.getPrice();//new BigDecimal(priceCounter + sale.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                    //The item being tracked. If null entire time, just don't add it. We don't need to give the player an empty item.
                    ItemStack itemPurchased = null;

                    itemLoop:
                    for (int i = 0; i < sale.getAmount(); i++) {

                        //We are getting more than we need, stop!!
                        //OR
                        //We don't have money to keep going. STOP!!

                        if (tempTotalAmountCounter > amount || tempTotalPriceCounter > limit) {
                            tempTotalAmountCounter--;
                            tempTotalPriceCounter -= sale.getPrice();
                            break itemLoop;
                        }

                        //Less than or equal the limit, we are able to make the purchase.

                        //In theory, we 'purchased' it. Now promise the item.
                        if (itemPurchased == null) {
                            itemPurchased = new ItemStack(sale.getItemStack().getType(), 1); //Only buying one!
                            if (ingredient.getItemMeta() instanceof PotionMeta) {
                                itemPurchased.setItemMeta(ingredient.getItemMeta());
                            }
                        } else
                            itemPurchased.setAmount(itemPurchased.getAmount() + 1);

                        if (i != sale.getAmount() - 1) {
                            tempTotalAmountCounter = tempTotalAmountCounter + 1; //Incrementor
                            tempTotalPriceCounter = tempTotalPriceCounter + sale.getPrice(); //new BigDecimal(tempTotalPriceCounter + sale.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); //Incrementor
                        }
                    }

                    //If the item was purchased, then make sure we update the sale to show the correct item.
                    if (itemPurchased != null) {
                        itemsPurchased.add(itemPurchased); //Add to array of items purchased to give to player.
                        saleMap.put(sale, sale.getAmount() - itemPurchased.getAmount()); //For updating purposes after the loop ends.
                        priceCounter = tempTotalPriceCounter; //new BigDecimal(tempTotalPriceCounter).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        amountCounter = tempTotalAmountCounter;

                    } //The item was never purchased, this means that the person could not afford it or we've reached the maximum quantity.
                    else break;
                }

                //Could not afford
                if (itemsPurchased.isEmpty()) {
                    playerSender.sendMessage(ChatColor.RED + "You could not afford any of the items in the market.");
                    return false;
                }

                //Not enough items but its okay.
                if (amount > amountCounter) {
                    //   playerSender.sendMessage(ChatColor.RED + "You didn't receive the amount you requested for. However, enjoy a portion of what you were able to purchase.");
                    playerSender.sendMessage(ChatColor.RED + "Only " + amountCounter + " " + name +
                            "remains in the economy.");
                    return false;
                }

                //Give items and count items dropped
                ArrayList<HashMap<Integer, ItemStack>> droppedItems = new ArrayList<>();
                int droppedItemsNum = 0;
                for (ItemStack itemPurchased : itemsPurchased) {
                    HashMap<Integer, ItemStack> droppedItem = playerSender.getInventory().addItem(itemPurchased);
                    if (droppedItem != null && !droppedItem.isEmpty()) {
                        droppedItems.add(droppedItem);
                        ItemStack itemStack = droppedItem.get(0);
                        droppedItemsNum += itemStack.getAmount();
                    }
                }

                //Drop items if need be.
                if (droppedItems.size() > 0) {
                    playerSender.sendMessage(ChatColor.RED + "You were not able to fit " + droppedItemsNum + " " + name + "into your inventory. They have been dropped on the ground.");

                    for (HashMap<Integer, ItemStack> map : droppedItems) {
                        for (ItemStack droppedItem : map.values()) {
                            playerSender.getWorld().dropItem(playerSender.getLocation(), droppedItem);
                        }
                    }
                }


                //Update sales.
                for (Iterator<Map.Entry<Sale, Integer>> entryIterator = saleMap.entrySet().iterator(); entryIterator.hasNext(); ) {
                    Map.Entry<Sale, Integer> entry = entryIterator.next();
                    Sale sale = entry.getKey();
                    int leftoverAmount = entry.getValue();

                    Bank seller = LostShardPlugin.getBankManager().wrap(sale.getSellerUUID());

                    double addedCurrencyRaw = ((sale.getAmount() - leftoverAmount) * sale.getPrice());
                    BigDecimal addedCurrency = new BigDecimal(addedCurrencyRaw).setScale(2, RoundingMode.HALF_UP);
                    double newCurrencyRaw = seller.getCurrency() + addedCurrencyRaw;
                    BigDecimal newCurrency = new BigDecimal(newCurrencyRaw).setScale(2, RoundingMode.HALF_UP);

                    seller.setCurrency(newCurrencyRaw);

                    OfflinePlayer sellerPlayer = Bukkit.getOfflinePlayer(seller.getPlayerUUID());
                    if (sellerPlayer.isOnline())
                        sellerPlayer.getPlayer().sendMessage(ChatColor.GOLD + "A player has bought " + (sale.getAmount() - leftoverAmount) + " " + name + "from you for " + addedCurrencyRaw + " gold. Your new balance is " + seller.getCurrency() + ".");
                    sale.setAmount(leftoverAmount);
                    if(sale.getAmount() == 0)
                        LostShardPlugin.getSaleManager().removeSale(sale);
                }

                //Update
                double removedCurrencyRaw = buyerBank.getCurrency() - priceCounter;
                BigDecimal removedCurrency = new BigDecimal(removedCurrencyRaw).setScale(2, RoundingMode.HALF_UP);
                BigDecimal price = new BigDecimal(priceCounter).setScale(2, RoundingMode.HALF_UP);

                buyerBank.setCurrency(removedCurrencyRaw); //Takes money away!!!!
                playerSender.sendMessage(ChatColor.GRAY + "You have bought " + amountCounter + " " + name + "for " + priceCounter + ". Your new balance is " + buyerBank.getCurrency() + ".");

                //I think we are done o_o

            }
        }
        return true;
    }


}
