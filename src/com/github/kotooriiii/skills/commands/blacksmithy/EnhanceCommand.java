package com.github.kotooriiii.skills.commands.blacksmithy;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.skills.Skill;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.skills.events.BlacksmithySkillEvent;
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

import static com.github.kotooriiii.data.Maps.*;

public class EnhanceCommand implements CommandExecutor {

    final int STAMINA_COST = 10;
    final int ADDED_XP = 100;

    final int MAXIMUM_ENHANCE = 4;

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
        InventoryUtil invHelper = new InventoryUtil(playerSender, ingredients, "to enhance this item", 1, false);

        //If inventory doesn't have the necessary ingredients.
        if (!invHelper.hasIngredients())
            return false;

        //Make sure player has the stats necessary
        Stat stat = Stat.wrap(playerUUID);
        if (stat.getStamina() < STAMINA_COST) {
            playerSender.sendMessage(ERROR_COLOR + "You must have at least " + STAMINA_COST + " stamina to enhance an item.");
            return false;
        }

        //Check if already maxed out
        if (!hasMoreEnchants(mainHand, playerSender) || getBlacksmithyLevelNeeded(mainHand) == -1) {
            playerSender.sendMessage(ERROR_COLOR + "The item has reached the highest level to be enhanced.");
            return false;
        }


        //Get the skill object
        Skill blacksmithy =  LostShardPlugin.getSkillManager().getSkillPlayer(playerUUID).getActiveBuild().getBlacksmithy();

        //Calculate chance
        int level = (int) blacksmithy.getLevel();

        if (!hasBlacksmithyLevel(mainHand, level)) {
            playerSender.sendMessage(ERROR_COLOR + "You aren't high enough level to enhance this item.");
            return false;
        }


        BlacksmithySkillEvent event = new BlacksmithySkillEvent(playerSender, BlacksmithyType.ENHANCE);
        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled())
            return false;

        //Harden
        enchant(mainHand,playerSender);
        playerSender.sendMessage(ChatColor.GOLD + "You enhance the item.");


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
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_SHOVEL:
            case DIAMOND_HOE:
                //GOLD
            case GOLDEN_PICKAXE:
            case GOLDEN_AXE:
            case GOLDEN_SHOVEL:
            case GOLDEN_HOE:
                if(nextLevel == 1)
                    return 50;
                else if(nextLevel == 2)
                    return 60;
                else if(nextLevel == 3)
                    return 70;
                else if(nextLevel == 4)
                    return 80;
                else if(nextLevel == 5)
                    return 100;
                else
                    return -1;
                //IRON
            case IRON_PICKAXE:
            case IRON_AXE:
            case IRON_SHOVEL:
            case IRON_HOE:
                if(nextLevel == 1)
                    return 25;
                else if(nextLevel == 2)
                    return 30;
                else if(nextLevel == 3)
                    return 40;
                else
                    return -1;
                //STONE
            case STONE_PICKAXE:
            case STONE_AXE:
            case STONE_SHOVEL:
            case STONE_HOE:
                if(nextLevel == 1)
                    return 10;
                else if(nextLevel == 2)
                    return 15;
                else
                    return -1;
                //WOODEN
            case WOODEN_PICKAXE:
            case WOODEN_AXE:
            case WOODEN_SHOVEL:
            case WOODEN_HOE:
                if (nextLevel == 1)
                    return 5;
                else
                    return -1;
        }

        return 0;
    }

    private boolean isTool(ItemStack itemStack) {
        Material material = itemStack.getType();

        switch (material) {

            //DIAMOND
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_SHOVEL:
            case DIAMOND_HOE:
                //GOLD
            case GOLDEN_PICKAXE:
            case GOLDEN_AXE:
            case GOLDEN_SHOVEL:
            case GOLDEN_HOE:
                //IRON
            case IRON_PICKAXE:
            case IRON_AXE:
            case IRON_SHOVEL:
            case IRON_HOE:
                //STONE
            case STONE_PICKAXE:
            case STONE_AXE:
            case STONE_SHOVEL:
            case STONE_HOE:
                //WOODEN
            case WOODEN_PICKAXE:
            case WOODEN_AXE:
            case WOODEN_SHOVEL:
            case WOODEN_HOE:
                return true;
        }
        return false;
    }

    private boolean hasMoreEnchants(ItemStack itemStack, Player player) {

        int MAXIMUM_ENHANCE_FINAL = MAXIMUM_ENHANCE;
        Clan clan = LostShardPlugin.getClanManager().getClan(player.getUniqueId());
        if(clan != null && clan.hasEnhanceTimer())
            MAXIMUM_ENHANCE_FINAL = 5;

        int efficiencyLevel = itemStack.getEnchantmentLevel(Enchantment.DIG_SPEED);
        int unbreakingLevel = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);

        int efficiencyMaxLevel = Enchantment.DIG_SPEED.getMaxLevel();
        int unbreakingMaxLevel = Enchantment.DURABILITY.getMaxLevel();

        if ((efficiencyLevel < MAXIMUM_ENHANCE_FINAL && efficiencyLevel < efficiencyMaxLevel) || (unbreakingLevel < MAXIMUM_ENHANCE_FINAL && unbreakingLevel < unbreakingMaxLevel))
            return true;
        return false;
    }

    private int getEnhanceLevel(ItemStack itemStack) {

        int efficiencyLevel = itemStack.getEnchantmentLevel(Enchantment.DIG_SPEED);
        int unbreakingLevel = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);

        int efficiencyMaxLevel = Enchantment.DIG_SPEED.getMaxLevel();
        int unbreakingMaxLevel = Enchantment.DURABILITY.getMaxLevel();

        if (efficiencyLevel == unbreakingLevel)
            return efficiencyLevel;

        if (unbreakingLevel == unbreakingMaxLevel)
            return efficiencyLevel;
        if (efficiencyLevel == efficiencyMaxLevel)
            return unbreakingLevel;

        return efficiencyLevel < unbreakingLevel ? efficiencyLevel : unbreakingLevel;
    }

    private void enchant(ItemStack itemStack, Player player) {

        int MAXIMUM_ENHANCE_FINAL = MAXIMUM_ENHANCE;
        Clan clan =LostShardPlugin.getClanManager().getClan(player.getUniqueId());
        if (clan != null && clan.hasEnhanceTimer())
            MAXIMUM_ENHANCE_FINAL = 5;

        int nextLevel = getEnhanceLevel(itemStack) + 1;

        int efficiencyLevel = itemStack.getEnchantmentLevel(Enchantment.DIG_SPEED);
        int unbreakingLevel = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);

        int efficiencyMaxLevel = Enchantment.DIG_SPEED.getMaxLevel();
        int unbreakingMaxLevel = Enchantment.DURABILITY.getMaxLevel();

        if (efficiencyLevel < nextLevel && nextLevel <= MAXIMUM_ENHANCE_FINAL && nextLevel <= efficiencyMaxLevel) {
            itemStack.removeEnchantment(Enchantment.DIG_SPEED);
            itemStack.addEnchantment(Enchantment.DIG_SPEED, nextLevel);
            if(nextLevel == 4)
                itemStack.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
            if(nextLevel == 5)
            {
                itemStack.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 2);
                clan.broadcast(ChatColor.YELLOW + player.getName() + STANDARD_COLOR + " has exhausted the enhance buff!");
                clan.setEnhanceTimer(0);
            }
        }

        if (unbreakingLevel < nextLevel && nextLevel <= MAXIMUM_ENHANCE_FINAL && nextLevel <= unbreakingMaxLevel) {
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
            case 5:
                cost = 32;
                break;
        }

        switch (material) {

            //DIAMOND
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_SHOVEL:
            case DIAMOND_HOE:
                return new ItemStack[]{new ItemStack(Material.DIAMOND, cost)};

            //GOLD
            case GOLDEN_PICKAXE:
            case GOLDEN_AXE:
            case GOLDEN_SHOVEL:
            case GOLDEN_HOE:
                return new ItemStack[]{new ItemStack(Material.GOLD_INGOT, cost)};

            //IRON
            case IRON_PICKAXE:
            case IRON_AXE:
            case IRON_SHOVEL:
            case IRON_HOE:
                return new ItemStack[]{new ItemStack(Material.IRON_INGOT, cost)};

            //STONE
            case STONE_PICKAXE:
            case STONE_AXE:
            case STONE_SHOVEL:
            case STONE_HOE:
                return new ItemStack[]{new ItemStack(Material.COBBLESTONE, cost)};

            case WOODEN_PICKAXE:
            case WOODEN_AXE:
            case WOODEN_SHOVEL:
            case WOODEN_HOE:
                return new ItemStack[]{new ItemStack(Material.OAK_PLANKS, cost), new ItemStack(Material.ACACIA_PLANKS, cost), new ItemStack(Material.BIRCH_PLANKS, cost), new ItemStack(Material.DARK_OAK_PLANKS, cost),
                        new ItemStack(Material.JUNGLE_PLANKS, cost), new ItemStack(Material.SPRUCE_PLANKS, cost)};

        }
        return null;
    }
}
