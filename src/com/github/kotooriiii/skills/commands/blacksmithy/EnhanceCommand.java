package com.github.kotooriiii.skills.commands.blacksmithy;

import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.stats.Stat;
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

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class EnhanceCommand implements CommandExecutor {

    final int STAMINA_COST = 10;
    final int ADDED_XP = 25;

    final int MAXIUMUM_HARDEN = 2;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        //player
        if (!(sender instanceof Player))
            return false;

        //comand
        if (!cmd.getName().equalsIgnoreCase("enhance"))
            return false;


        //Declare early variables
        final Player playerSender = (Player) sender;
        final UUID playerUUID = playerSender.getUniqueId();

        //Item in slot
        ItemStack mainHand = playerSender.getInventory().getItemInMainHand();
        ItemMeta meta = mainHand.getItemMeta();


        //If ingredients don't exist or the item isn't able to take any damage
        if (!isTool(mainHand) || !(meta instanceof Damageable)) {
            playerSender.sendMessage(ERROR_COLOR + "This item is not able to be enhanced.");
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
        int nextLevel = getEnhanceLevel(mainHand) + 1;
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

    private boolean isTool(ItemStack itemStack) {
        Material material = itemStack.getType();

        switch (material) {

            //DIAMOND
            case DIAMOND_AXE:
            case DIAMOND_SHOVEL:
            case DIAMOND_HOE:
                //GOLD
            case GOLDEN_AXE:
            case GOLDEN_SHOVEL:
            case GOLDEN_HOE:
                //IRON
            case IRON_AXE:
            case IRON_SHOVEL:
            case IRON_HOE:
                //STONE
            case STONE_AXE:
            case STONE_SHOVEL:
            case STONE_HOE:
                //WOODEN
            case WOODEN_AXE:
            case WOODEN_SHOVEL:
            case WOODEN_HOE:
                return true;
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

    private int getEnhanceLevel(ItemStack itemStack) {

        int protectionLevel = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        int unbreakingLevel = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);

        int protectionMaxLevel = Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel();
        int unbreakingMaxLevel = Enchantment.DURABILITY.getMaxLevel();

        if (protectionLevel == unbreakingLevel)
            return protectionLevel;

        if (unbreakingLevel == unbreakingMaxLevel)
            return protectionLevel;
        if (protectionLevel == protectionMaxLevel)
            return unbreakingLevel;

        return protectionLevel < unbreakingLevel ? protectionLevel : unbreakingLevel;
    }

    private void enchant(ItemStack itemStack) {
        int nextLevel = getEnhanceLevel(itemStack) + 1;

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
        int cost = -1;

        switch (getEnhanceLevel(itemStack) + 1) {
            case 1:
                cost = 1;
                break;
            case 2:
                cost = 2;
                break;
            case 3:
                cost = 6;
                break;
            case 4:
                cost = 10;
                break;
        }

        switch (material) {

            //DIAMOND
            case DIAMOND_AXE:
            case DIAMOND_SHOVEL:
            case DIAMOND_HOE:
                return new ItemStack[]{new ItemStack(Material.DIAMOND, cost)};

            //GOLD
            case GOLDEN_AXE:
            case GOLDEN_SHOVEL:
            case GOLDEN_HOE:
                return new ItemStack[]{new ItemStack(Material.GOLD_INGOT, cost)};

            //IRON
            case IRON_AXE:
            case IRON_SHOVEL:
            case IRON_HOE:
                return new ItemStack[]{new ItemStack(Material.IRON_INGOT, cost)};

            //STONE
            case STONE_AXE:
            case STONE_SHOVEL:
            case STONE_HOE:
                return new ItemStack[]{new ItemStack(Material.COBBLESTONE, cost)};

            case WOODEN_AXE:
            case WOODEN_SHOVEL:
            case WOODEN_HOE:
                return new ItemStack[]{new ItemStack(Material.OAK_PLANKS, 1), new ItemStack(Material.ACACIA_PLANKS, 1), new ItemStack(Material.BIRCH_PLANKS, 1), new ItemStack(Material.DARK_OAK_PLANKS, 1),
                        new ItemStack(Material.JUNGLE_PLANKS, 1), new ItemStack(Material.SPRUCE_PLANKS, 1)};

        }
        return null;
    }
}