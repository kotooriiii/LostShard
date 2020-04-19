package com.github.kotooriiii.skills.commands.blacksmithy;

import net.md_5.bungee.chat.SelectorComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class InventoryUtil {
    private String message;
    private Player player;
    private ItemStack[] ingredients;

    private int counter = 0;
    private int maxCounter;
    private boolean checkAll;

    public InventoryUtil(Player player, ItemStack[] ingredients, String message, int maxCounter, boolean checkAll) {
        this.player = player;
        this.ingredients = ingredients;
        this.message = message;

        this.maxCounter = maxCounter;
        this.checkAll = checkAll;
    }

    public boolean hasIngredients() {
        ArrayList<HashMap<Integer, Integer>> pendingRemovalItems = new ArrayList<>();

        boolean hasIngredients = true;
        boolean isUnique = false;

        for (ItemStack ingredient : ingredients) {
            HashMap<Integer, Integer> indeces = hasIngredient(ingredient, true, false);
            if (indeces == null) {
                hasIngredients = false;
                continue;
            } else {
                isUnique=true;
            }
            pendingRemovalItems.add(indeces);

            if (!checkAll && isUnique)
                return hasIngredients;
        }

        return hasIngredients;
    }

    public boolean removeIngredients() {
        ArrayList<HashMap<Integer, Integer>> pendingRemovalItems = new ArrayList<>();
        PlayerInventory currentInventory = player.getInventory();

        boolean hasIngredients = true;
        boolean isUnique = false;

        for (ItemStack ingredient : ingredients) {
            HashMap<Integer, Integer> indeces = hasIngredient(ingredient, false, true);
            if (indeces == null) {
                hasIngredients = false;
                continue;
            } else {
                isUnique = true;
            }
            pendingRemovalItems.add(indeces);

            if (!checkAll && isUnique)
                break;

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

    private HashMap<Integer, Integer> hasIngredient(ItemStack ingredient, boolean message, boolean isRemoving) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        int amountRequired = ingredient.getAmount();
        Material materialType = ingredient.getType();

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

        if (isRemoving) {
            if (counterMoney < amountRequired) {
                if (message)
                    player.sendMessage(ERROR_COLOR + "You need " + amountRequired + " " + materialType.getKey().getKey().toLowerCase().replace("_", " ") + " " + this.message + ".");
                return null;

            }

        } else {
            if (counterMoney < amountRequired && counter < maxCounter) {

                if (message)
                    player.sendMessage(ERROR_COLOR + "You need " + amountRequired + " " + materialType.getKey().getKey().toLowerCase().replace("_", " ") + " " + this.message + ".");
                counter++;
                return null;

            }
        }


        return hashmap;
}

}
