package com.github.kotooriiii.skills.commands.blacksmithy;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.Skill;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.skills.events.BlacksmithySkillEvent;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.*;
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

    final int STAMINA_COST = 25;
    final int ADDED_XP = 100;

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
        InventoryUtil invHelper = new InventoryUtil(playerSender, ingredients, "to harden this item", 1, false);

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
        Skill blacksmithy = LostShardPlugin.getSkillManager().getSkillPlayer(playerUUID).getActiveBuild().getBlacksmithy();

        //Calculate chance
        int level = (int) blacksmithy.getLevel();

        if (!hasBlacksmithyLevel(mainHand, level)) {
            playerSender.sendMessage(ERROR_COLOR + "You aren't high enough level to harden this item.");
            return false;
        }

        BlacksmithySkillEvent event = new BlacksmithySkillEvent(playerSender, BlacksmithyType.HARDEN);
        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        //Harden
        if (!enchant(mainHand)) {
            playerSender.sendMessage(ERROR_COLOR + "You cannot add conflicting enchantments.");
            return false;
        }

        playerSender.sendMessage(ChatColor.GOLD + "You harden the item.");


        //Give rewards/xp/consequence.
        if (mainHand.getType().name().toLowerCase().startsWith("stone_") && level > 50) {

        } else if (mainHand.getType().name().toLowerCase().startsWith("iron_") && level > 75) {

        } else {
            blacksmithy.addXP(ADDED_XP);
        }

        stat.setStamina(stat.getStamina() - STAMINA_COST);
        invHelper.removeIngredients();

        if(LostShardPlugin.getAnimatorPackage().isAnimating(playerUUID))
        {
            playerSender.getWorld().spawnParticle(Particle.REDSTONE, playerSender.getLocation().add(0,1,0), 30, 0.5,0.5,0.5, new Particle.DustOptions(Color.fromRGB(0, 0, 0), 1f));
            playerSender.getWorld().playSound(playerSender.getLocation(), Sound.BLOCK_ANVIL_USE, 5,3);
        }
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

            //NETHERITE
            case NETHERITE_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:

            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
                if (nextLevel == 1)
                    return 80;
                else if (nextLevel == 2)
                    return 100;
                else return -1;
                //GOLD
            case GOLDEN_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
                if (nextLevel == 1)
                    return -1;
                else if (nextLevel == 2)
                    return -1;
                else return -1;
                //IRON
            case IRON_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
                if (nextLevel == 1)
                    return 50;
                else if (nextLevel == 2)
                    return 65;
                else return -1;
                //STONE
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
                if (nextLevel == 1)
                    return 25;
                else if (nextLevel == 2)
                    return 40;
                else
                    return -1;
        }

        return 0;
    }

    private boolean isArmor(ItemStack itemStack) {
        Material material = itemStack.getType();

        switch (material) {

            //netherite
            case NETHERITE_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:


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

        if (unbreakingLevel == unbreakingMaxLevel)
            return protectionLevel;
        if (protectionLevel == protectionMaxLevel)
            return unbreakingLevel;

        return protectionLevel < unbreakingLevel ? protectionLevel : unbreakingLevel;
    }

    private boolean enchant(ItemStack itemStack) {
        int nextLevel = getHardenLevel(itemStack) + 1;

        int protectionLevel = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        int unbreakingLevel = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);

        int protectionMaxLevel = Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel();
        int unbreakingMaxLevel = Enchantment.DURABILITY.getMaxLevel();


        if (protectionLevel < nextLevel && nextLevel <= MAXIUMUM_HARDEN && nextLevel <= protectionMaxLevel) {
            if (!itemStack.getItemMeta().hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.getItemMeta().hasConflictingEnchant(Enchantment.PROTECTION_ENVIRONMENTAL))
                return false;
            itemStack.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
            itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, nextLevel);
        }

        if (unbreakingLevel < nextLevel && nextLevel <= MAXIUMUM_HARDEN && nextLevel <= unbreakingMaxLevel) {
            if (!itemStack.getItemMeta().hasEnchant(Enchantment.DURABILITY) && itemStack.getItemMeta().hasConflictingEnchant(Enchantment.DURABILITY))
                return false;
            itemStack.removeEnchantment(Enchantment.DURABILITY);
            itemStack.addEnchantment(Enchantment.DURABILITY, nextLevel);
        }
        return true;
    }

    private ItemStack[] getCost(ItemStack itemStack) {

        Material material = itemStack.getType();
        int cost = getHardenLevel(itemStack) + 1;

        switch (material) {

            //NETHERITE
            case NETHERITE_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
                return new ItemStack[]{new ItemStack(Material.NETHERITE_INGOT, cost)};

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
