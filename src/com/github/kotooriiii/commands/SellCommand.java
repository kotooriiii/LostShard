package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Sale;
import com.github.kotooriiii.util.HelperMethods;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

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

            //If the command is sell
            if (cmd.getName().equalsIgnoreCase("sell")) {
                //No arguments regarding this command
                if (args.length != 3) {
                    playerSender.sendMessage(ChatColor.RED + "Did you mean " + "/sell (amount) (item) (price)"  + "?");
                    return false;
                }

//                String possibleAmount = args[0];
//                String possibleItem = args[1];
//                String possibleLimit = args[2];

                //Check first argument
                if (!NumberUtils.isNumber(args[0])) {
                    playerSender.sendMessage(ChatColor.RED + "You must use positive integers to sell items.");
                    return true;
                }

                if (args[0].contains(".")) {
                    playerSender.sendMessage(ChatColor.RED + "You must use positive integers to sell items.");
                    return true;
                }

                //Check second argument
                ItemStack ingredient = getItem(playerSender, args[1].substring(0,1).toUpperCase() + args[1].substring(1).toLowerCase());
                if (ingredient == null)
                    return false;

                //Check third argument
                if (!NumberUtils.isNumber(args[2])) {
                    playerSender.sendMessage(ChatColor.RED + "/sell (amount) (item) (price)");
                    return true;
                }

                //User input restrictions passed
                int amount = Integer.parseInt(args[0]);
                if (amount <= 0) {
                    playerSender.sendMessage(ChatColor.RED + "You must use positive integers to sell items.");
                    return false;
                }
                double totalPriceRaw = Double.parseDouble(args[2]);
                BigDecimal totalPrice = new BigDecimal(args[2]).setScale(2, BigDecimal.ROUND_HALF_UP);

                ingredient.setAmount(amount);
                ItemStack[] ingredients = new ItemStack[]{ingredient};
                if (!hasIngredients(playerSender, ingredients))
                    return false;

                removeIngredients(playerSender, ingredients);

                double individualRawPrice = (double) (totalPriceRaw) / (double) amount;
                BigDecimal individualPrice = new BigDecimal(individualRawPrice).setScale(2, BigDecimal.ROUND_HALF_UP);


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

                playerSender.sendMessage(ChatColor.GRAY + "You have put " + amount + " " + name + "on the market for " + totalPriceRaw + " gold.");// In simpler terms, you're selling 1 " + name + "for " + individualRawPrice + ".");
                Sale sale = new Sale(playerUUID, ingredient, amount, individualRawPrice);

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

        ItemMeta meta = itemStack.getItemMeta();


        int counterMoney = 0;

        //Inventory slot index , int amount
        HashMap<Integer, Integer> hashmap = new HashMap<>();

        for (int i = 0; i < contents.length; i++) {
            ItemStack iteratingItem = contents[i];
            if (iteratingItem == null || !materialType.equals(iteratingItem.getType()))
                continue;

            if (meta instanceof PotionMeta) {
                if (!(iteratingItem.getItemMeta() instanceof PotionMeta))
                    continue;
                if (!((PotionMeta) meta).getBasePotionData().equals(((PotionMeta) iteratingItem.getItemMeta()).getBasePotionData()))
                    continue;
            }
            if (!itemStack.getEnchantments().isEmpty())
                continue;
            if (itemStack.getType().equals(Material.SHULKER_BOX))
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
            player.sendMessage(ChatColor.RED + "You only have " + counterMoney + " " + materialType.getKey().getKey().substring(0,1).toUpperCase() +materialType.getKey().getKey().substring(1).toLowerCase() + ".");
            return null;
        }
        return hashmap;
    }

    public static ItemStack getItem(Player playerSender, String itemName) {
        //Declare null
        ItemStack itemStack = null;
        Material material = null;

        //Try to get potion out of it
        itemStack = potionHelper(itemName);
        //Try longer version of potion
        if (itemStack == null)
            itemStack = potionLongHelper(itemName);

        //If we found a potion, set the item as potion
        if (itemStack != null)
            material = Material.POTION;

        //No potion found, keep looking
        if (material == null) {
            if (itemName.equalsIgnoreCase("cobble")) //key premade
                material = Material.COBBLESTONE;

            //If still null keep going
            if (material == null) //If not key words we previously made then search all material system
                material = Material.matchMaterial(itemName.replace(" ", "_").toUpperCase()); //Find the material with that name!

            //Still somehow null.. make suggestions
            if (material == null) {

                playerSender.sendMessage(ChatColor.RED + "Item '" + itemName + "' not found.");

                Material[] materials = HelperMethods.getNearestMaterials(itemName, 3);
                if (materials == null || materials.length == 0) {
                    playerSender.sendMessage(ChatColor.RED + "No relatable items were found.");
                    return null;
                }
                String[] materialNames = new String[materials.length];
                for (int i = 0; i < materialNames.length; i++) {
                    if (materials[i] == null)
                        break;
                    materialNames[i] = materials[i].getKey().getKey().toLowerCase();
                }
                String builder = HelperMethods.stringBuilder(materialNames, 0, ", ", ", or ");
                playerSender.sendMessage(ChatColor.RED  + "Did you possibly mean one of these items?:\n" + builder);
                return null;
            }
        }
        if (itemStack == null)
            itemStack = new ItemStack(material, 1);
        return itemStack;
    }

    public static ItemStack potionLongHelper(String name) {
        ItemStack itemStack = new ItemStack(Material.POTION, 1);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();

        String potionName = name.substring(0, name.length() - 1).toUpperCase().replace(" ", "_");
        String suffix = name.substring(name.length() - 1, name.length());

        for (PotionType type : PotionType.values()) {
            if (potionName.equalsIgnoreCase(type.name())) {
                potionMeta.setBasePotionData(new PotionData(type));
                break;
            }
        }

        if (!suffix.isEmpty()) {
            if (suffix.equalsIgnoreCase("e")) {
                if(potionMeta.getBasePotionData().getType().isExtendable())
                    potionMeta.setBasePotionData(new PotionData(potionMeta.getBasePotionData().getType(), true, false));
            }
            if (suffix.equalsIgnoreCase("2")) {
                if (potionMeta.getBasePotionData().getType().isUpgradeable())
                    potionMeta.setBasePotionData(new PotionData(potionMeta.getBasePotionData().getType(), false, true));
            }
            if (suffix.equalsIgnoreCase("1"))
                ;
        }

        if (potionMeta.getBasePotionData().getType().equals(PotionType.UNCRAFTABLE))
            return null;
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }

    public static ItemStack potionHelper(String name) {
        ItemStack itemStack = new ItemStack(Material.POTION, 1);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();

        String potionName = name.substring(0, name.length() - 1).toUpperCase().replace(" ", "_");
        String suffix = name.substring(name.length() - 1, name.length());

        switch (potionName.toLowerCase()) {
            case "swp":
                potionMeta.setBasePotionData(new PotionData(PotionType.SPEED));
                break;
            case "strp":
                potionMeta.setBasePotionData(new PotionData(PotionType.STRENGTH));
                break;
            case "frp":
                potionMeta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE));
                break;
        }

        if (!suffix.isEmpty()) {
            if (suffix.equalsIgnoreCase("e")) {
                if(potionMeta.getBasePotionData().getType().isExtendable())
                potionMeta.setBasePotionData(new PotionData(potionMeta.getBasePotionData().getType(), true, false));
            }
            if (suffix.equalsIgnoreCase("2")) {
                if (potionMeta.getBasePotionData().getType().isUpgradeable())
                    potionMeta.setBasePotionData(new PotionData(potionMeta.getBasePotionData().getType(), false, true));
            }
            if (suffix.equalsIgnoreCase("1"))
                ;
        }

        if (potionMeta.getBasePotionData().getType().equals(PotionType.UNCRAFTABLE))
            return null;

        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }
}
