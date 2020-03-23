package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Sale;
import com.github.kotooriiii.util.HelperMethods;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class SellCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();

            //If the command is balance
            if (cmd.getName().equalsIgnoreCase("sell")) {
                //No arguments regarding this command
                if (args.length != 3) {
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean " + COMMAND_COLOR + "/sell (amount) (item) (total price)" + ERROR_COLOR + "?");
                    return false;
                }

//                String possibleAmount = args[0];
//                String possibleItem = args[1];
//                String possibleLimit = args[2];

                //Check first argument
                if (!NumberUtils.isNumber(args[0])) {
                    playerSender.sendMessage(ERROR_COLOR + "The provided amount you'd like to sell is not a number.");
                    return true;
                }

                if (args[0].contains(".")) {
                    playerSender.sendMessage(ERROR_COLOR + "You can only sell full, intact, unbroken items.");
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
                    playerSender.sendMessage(ERROR_COLOR + "Did you possibly mean to buy one of these items?:\n" + builder);
                    return true;
                }

                //Check third argument
                if (!NumberUtils.isNumber(args[2])) {
                    playerSender.sendMessage(ERROR_COLOR + "The total price you provided is not a number.");
                    return true;
                }

                //User input restrictions passed
                int amount = Integer.parseInt(args[0]);
                BigDecimal totalPrice = new BigDecimal(args[2]).setScale(2, RoundingMode.HALF_UP);

                ItemStack ingredient = new ItemStack(material, amount);
                ItemStack[] ingredients = new ItemStack[]{ingredient};
                if (!hasIngredients(playerSender, ingredients))
                    return false;

                removeIngredients(playerSender, ingredients);

                double individualRawPrice = (double)  totalPrice.doubleValue()/amount;
                BigDecimal individualPrice = new BigDecimal(individualRawPrice).setScale(2, RoundingMode.HALF_UP);


                playerSender.sendMessage(STANDARD_COLOR + "You have added " + amount + " " + material.getKey().getKey().toLowerCase().replace("_", " ") + " to the market for a total price of " + totalPrice + ". In simpler terms, you're selling 1 " + material.getKey().getKey().toLowerCase().replace("_", " ") + " for " + individualPrice + ".");
                Sale sale = new Sale(playerUUID, material, amount, individualPrice.doubleValue());

            }
        }
        return true;
    }

    public boolean hasIngredients(Player player, ItemStack[] ingredients) {
        ArrayList<HashMap<Integer, Integer>> pendingRemovalItems = new ArrayList<>();

        boolean hasIngredients = true;
        for (ItemStack ingredient : ingredients) {
            HashMap<Integer, Integer> indeces = hasIngredient(player, ingredient);
            if (indeces == null) {
                hasIngredients = false;
                continue;
            }
            pendingRemovalItems.add(indeces);
        }

        return hasIngredients;
    }

    public boolean removeIngredients(Player player, ItemStack[] ingredients) {
        ArrayList<HashMap<Integer, Integer>> pendingRemovalItems = new ArrayList<>();
        PlayerInventory currentInventory = player.getInventory();

        boolean hasIngredients = true;
        for (ItemStack ingredient : ingredients) {
            HashMap<Integer, Integer> indeces = hasIngredient(player, ingredient);
            if (indeces == null) {
                hasIngredients = false;
                continue;
            }
            pendingRemovalItems.add(indeces);
        }

        if (hasIngredients) {
            for (HashMap<Integer, Integer> indeces : pendingRemovalItems) {
                for (Map.Entry entry : indeces.entrySet()) {
                    int key = (Integer) entry.getKey();
                    ItemStack itemStack = currentInventory.getItem(key);
                    int value = (Integer) entry.getValue();
                    if (value == 0)
                        currentInventory.setItem(key, null);
                    else {
                        itemStack.setAmount(value);
                        currentInventory.setItem(key, itemStack);
                    }
                }
            }
        }

        return hasIngredients;
    }

    public HashMap<Integer, Integer> hasIngredient(Player player, ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        int amountRequired = itemStack.getAmount();
        Material materialType = itemStack.getType();

        int counterMoney = 0;

        //Inventory slot index , int amount
        HashMap<Integer, Integer> hashmap = new HashMap<>();

        for (int i = 0; i < contents.length; i++) {
            ItemStack iteratingItem = contents[i];
            if (iteratingItem == null || !materialType.equals(iteratingItem.getType()))
                continue;
            int iteratingCount = iteratingItem.getAmount();
            int tempTotal = iteratingCount + counterMoney;

            if (amountRequired >= tempTotal) {
                counterMoney += iteratingCount;
                hashmap.put(new Integer(i), 0);
            } else if (amountRequired < tempTotal) {
                int tempLeftover = tempTotal - amountRequired;
                hashmap.put(new Integer(i), tempLeftover);
                counterMoney = amountRequired;
                break;
            }
        }

        if (counterMoney < amountRequired) {
            player.sendMessage(ERROR_COLOR + "You don't have " + amountRequired + " " + materialType.getKey().getKey().toLowerCase() + " to sell.");
            return null;
        }
        return hashmap;
    }
}
