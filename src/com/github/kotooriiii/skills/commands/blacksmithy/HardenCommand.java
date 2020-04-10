package com.github.kotooriiii.skills.commands.blacksmithy;

import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class HardenCommand implements CommandExecutor {

    final int STAMINA_COST = 10;
    final int ADDED_XP = 25;

    final int MAXIUMUM_HARDEN = 2;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        //player
        if (!(sender instanceof Player))
            return false;

        //comand
        if (!cmd.getName().equalsIgnoreCase("harden"))
            return false;


        //Declare early variables
        final Player playerSender = (Player) sender;
        final UUID playerUUID = playerSender.getUniqueId();

        //Item in slot
        ItemStack mainHand = playerSender.getInventory().getItemInMainHand();
        ItemMeta meta = mainHand.getItemMeta();


        //If ingredients don't exist or the item isn't able to take any damage
        if (!isArmor(mainHand) || !(meta instanceof Damageable)) {
            playerSender.sendMessage(ERROR_COLOR + "This item is not able to be hardened.");
            return false;
        }

        //Get ingredients for item
        ItemStack[] ingredients = getCost(mainHand);

        //Inventory helper and construct error message
        InventoryUtil invHelper = new InventoryUtil(playerSender, ingredients, "to harden this item");

        //If inventory doesn't have the necessary ingredients.
        if (!invHelper.hasIngredients())
            return false;

        //Make sure player has the stats necessary
        Stat stat = Stat.wrap(playerUUID);
        if (stat.getStamina() < STAMINA_COST) {
            playerSender.sendMessage(ERROR_COLOR + "You must have at least " + STAMINA_COST + " stamina to harden an item.");
            return false;
        }

        //Check if already maxed out
        if (!hasMoreEnchants(mainHand)) {
            playerSender.sendMessage(ERROR_COLOR + "The item has reached the highest level to be hardened.");
            return false;
        }


        //Get the skill object
        SkillPlayer.Skill blacksmithy = SkillPlayer.wrap(playerUUID).getBlacksmithy();

        //Calculate chance
        int level = (int) blacksmithy.getLevel();

        if (!hasBlacksmithyLevel(mainHand, level)) {
            playerSender.sendMessage(ERROR_COLOR + "You aren't high enough level to harden this item.");
            return false;
        }


        //Harden
        enchant(mainHand);
        playerSender.sendMessage(ChatColor.GOLD + "You harden the item.");


        //Give rewards/xp/consequence.
        blacksmithy.addXP(ADDED_XP);
        stat.setStamina(stat.getStamina() - STAMINA_COST);
        invHelper.removeIngredients();
        return true;
    }

    private boolean hasBlacksmithyLevel(ItemStack mainHand, int level) {

        if (level < getBlacksmithyLevelNeeded(mainHand))
            return false;
        return true;
    }

    private int getBlacksmithyLevelNeeded(ItemStack mainHand) {
        int nextLevel = getHardenLevel(mainHand) + 1;
        Material material = mainHand.getType();

        switch (material) {

            //DIAMOND
            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
                if (nextLevel == 1)
                    return 80;
                else if (nextLevel == 2)
                    return 100;
                //GOLD
            case GOLDEN_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
                if (nextLevel == 1)
                    return -1;
                else if (nextLevel == 2)
                    return -1;
                //IRON
            case IRON_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
                if (nextLevel == 1)
                    return 50;
                else if (nextLevel == 2)
                    return 65;
                //STONE
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
                if (nextLevel == 1)
                    return 25;
                else if (nextLevel == 2)
                    return 40;
        }

        return -1;
    }

    private boolean isArmor(ItemStack itemStack) {
        Material material = itemStack.getType();

        switch (material) {

            //DIAMOND

            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:

                //IRON
            case IRON_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:

                //STONE
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
                return true;

            //GOLD
            case GOLDEN_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
                return false;
        }
        return false;
    }

    private boolean hasMoreEnchants(ItemStack itemStack) {
        int protectionLevel = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        int unbreakingLevel = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);

        int protectionMaxLevel = Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel();
        int unbreakingMaxLevel = Enchantment.DURABILITY.getMaxLevel();

        if ((protectionLevel < MAXIUMUM_HARDEN && protectionLevel < protectionMaxLevel) || (unbreakingLevel < MAXIUMUM_HARDEN && unbreakingLevel < unbreakingMaxLevel))
            return true;
        return false;
    }

    private int getHardenLevel(ItemStack itemStack) {

        int protectionLevel = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        int unbreakingLevel = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);

        int protectionMaxLevel = Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel();
        int unbreakingMaxLevel = Enchantment.DURABILITY.getMaxLevel();

        if (protectionLevel == unbreakingLevel)
            return protectionLevel;

        if(unbreakingLevel == unbreakingMaxLevel)
            return protectionLevel;
        if(protectionLevel == protectionMaxLevel)
            return unbreakingLevel;

        return protectionLevel < unbreakingLevel ? protectionLevel : unbreakingLevel;
    }

    private void enchant(ItemStack itemStack) {
        int nextLevel = getHardenLevel(itemStack) + 1;

        int protectionLevel = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        int unbreakingLevel = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);

        int protectionMaxLevel = Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel();
        int unbreakingMaxLevel = Enchantment.DURABILITY.getMaxLevel();

        if (protectionLevel < nextLevel && nextLevel <= MAXIUMUM_HARDEN && nextLevel <= protectionMaxLevel) {
            itemStack.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
            itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, nextLevel);
        }

        if (unbreakingLevel < nextLevel && nextLevel <= MAXIUMUM_HARDEN && nextLevel <= unbreakingMaxLevel) {
            itemStack.removeEnchantment(Enchantment.DURABILITY);
            itemStack.addEnchantment(Enchantment.DURABILITY, nextLevel);
        }

    }

    private ItemStack[] getCost(ItemStack itemStack) {

        Material material = itemStack.getType();
        int cost = getHardenLevel(itemStack) + 1;

        switch (material) {

            //DIAMOND
            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
                return new ItemStack[]{new ItemStack(Material.DIAMOND, cost)};

            //GOLD
            case GOLDEN_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
                return new ItemStack[]{new ItemStack(Material.GOLD_INGOT, cost)};

            //IRON
            case IRON_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
                return new ItemStack[]{new ItemStack(Material.IRON_INGOT, cost)};

            //STONE
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
                return new ItemStack[]{new ItemStack(Material.COBBLESTONE, cost)};

        }
        return null;
    }
}
