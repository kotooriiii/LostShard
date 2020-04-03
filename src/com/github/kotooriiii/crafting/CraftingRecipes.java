package com.github.kotooriiii.crafting;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingRecipes {
    public static void initRecipes() {
        initChainArmor();
    }

    private static void initChainArmor() {
        ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET, 1);
        ItemStack chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
        ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
        ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS, 1);



        // create a NamespacedKey for your recipe
        NamespacedKey helmetKey = new NamespacedKey(LostShardPlugin.plugin, "custom_chainmail_helmet");
        NamespacedKey chestplateKey = new NamespacedKey(LostShardPlugin.plugin, "custom_chainmail_chestplate");
        NamespacedKey leggingsKey = new NamespacedKey(LostShardPlugin.plugin, "custom_chainmail_leggings");
        NamespacedKey bootsKey = new NamespacedKey(LostShardPlugin.plugin, "custom_chainmail_boots");

// Create our custom recipe variable
        ShapedRecipe helmetRecipe = new ShapedRecipe(helmetKey, helmet);
        ShapedRecipe chestplateRecipe = new ShapedRecipe(chestplateKey, chestplate);
        ShapedRecipe leggingsRecipe = new ShapedRecipe(leggingsKey, leggings);
        ShapedRecipe bootsRecipe = new ShapedRecipe(bootsKey, boots);


        helmetRecipe.shape("###", "# #");
        //helmetRecipe.shape("   ","###", "# #");

        chestplateRecipe.shape("# #", "###", "###");
        leggingsRecipe.shape("###", "# #", "# #");

        //bootsRecipe.shape("   ", "# #", "# #");
        bootsRecipe.shape("# #", "# #");

        helmetRecipe.setIngredient('#', Material.COBBLESTONE);
        chestplateRecipe.setIngredient('#', Material.COBBLESTONE);
        leggingsRecipe.setIngredient('#', Material.COBBLESTONE);
        bootsRecipe.setIngredient('#', Material.COBBLESTONE);

// Finally, add the recipe to the bukkit recipes
        Bukkit.addRecipe(helmetRecipe);
        Bukkit.addRecipe(chestplateRecipe);
        Bukkit.addRecipe(leggingsRecipe);
        Bukkit.addRecipe(bootsRecipe);


    }
}
