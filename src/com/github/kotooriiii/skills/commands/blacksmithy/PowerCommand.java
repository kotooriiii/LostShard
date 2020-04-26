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

public class PowerCommand implements CommandExecutor {

    final int STAMINA_COST = 10;
    final int ADDED_XP = 125;

    final int MAXIMUM_POWER = 4;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        //player
        if (!(sender instanceof Player))
            return false;

        //comand
        if (!cmd.getName().equalsIgnoreCase("power"))
            return false;


        //Declare early variables
        final Player playerSender = (Player) sender;
        final UUID playerUUID = playerSender.getUniqueId();

        //Item in slot
        ItemStack mainHand = playerSender.getInventory().getItemInMainHand();
        ItemMeta meta = mainHand.getItemMeta();


        //If ingredients don't exist or the item isn't able to take any damage
        if (!isBow(mainHand) || !(meta instanceof Damageable)) {
            playerSender.sendMessage(ERROR_COLOR + "This item is not able to be powered.");
            return false;
        }

        //Get ingredients for item
        ItemStack[] ingredients = getCost(mainHand);

        //Inventory helper and construct error message
        InventoryUtil invHelper = new InventoryUtil(playerSender, ingredients, "to power this item", 1, false);

        //If inventory doesn't have the necessary ingredients.
        if (!invHelper.hasIngredients())
            return false;

        //Make sure player has the stats necessary
        Stat stat = Stat.wrap(playerUUID);
        if (stat.getStamina() < STAMINA_COST) {
            playerSender.sendMessage(ERROR_COLOR + "You must have at least " + STAMINA_COST + " stamina to power an item.");
            return false;
        }

        //Check if already maxed out
        if (!hasMoreEnchants(mainHand) || getBlacksmithyLevelNeeded(mainHand) == -1) {
            playerSender.sendMessage(ERROR_COLOR + "The item has reached the highest level to be powered.");
            return false;
        }


        //Get the skill object
        SkillPlayer.Skill blacksmithy = SkillPlayer.wrap(playerUUID).getBlacksmithy();

        //Calculate chance
        int level = (int) blacksmithy.getLevel();

        if (!hasBlacksmithyLevel(mainHand, level)) {
            playerSender.sendMessage(ERROR_COLOR + "You aren't high enough level to power this item.");
            return false;
        }


        //Harden
        enchant(mainHand);
        playerSender.sendMessage(ChatColor.GOLD + "You power the item.");


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
        int nextLevel = getPowerLevel(mainHand) + 1;
        Material material = mainHand.getType();

        switch (material) {
            case BOW:
                if (nextLevel == 1)
                    return 50;
                else if (nextLevel == 2)
                    return 60;
                else if (nextLevel == 3)
                    return 70;
                else if (nextLevel == 4)
                    return 80;
                else
                    return -1;
        }

        return 0;
    }

    private boolean isBow(ItemStack itemStack) {
        Material material = itemStack.getType();

        switch (material) {
            case BOW:
                return true;
        }
        return false;
    }

    private boolean hasMoreEnchants(ItemStack itemStack) {
        int powerLevel = itemStack.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);

        int powerMaxLevel = Enchantment.ARROW_DAMAGE.getMaxLevel();

        if ((powerLevel < MAXIMUM_POWER && powerLevel < powerMaxLevel))
            return true;
        return false;
    }

    private int getPowerLevel(ItemStack itemStack) {

        int arrowLevel = itemStack.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
        return arrowLevel;
    }

    private void enchant(ItemStack itemStack) {
        int nextLevel = getPowerLevel(itemStack) + 1;

        int powerLevel = itemStack.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
        int powerMaxLevel = Enchantment.ARROW_DAMAGE.getMaxLevel();

        if (powerLevel < nextLevel && nextLevel <= MAXIMUM_POWER && nextLevel <= powerMaxLevel) {
            itemStack.removeEnchantment(Enchantment.ARROW_DAMAGE);
            itemStack.addEnchantment(Enchantment.ARROW_DAMAGE, nextLevel);
        }
    }

    private ItemStack[] getCost(ItemStack itemStack) {

        Material material = itemStack.getType();
        int cost = -1;

        switch (getPowerLevel(itemStack) + 1) {
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
            case BOW:
                return new ItemStack[]{new ItemStack(Material.DIAMOND, cost)};
        }
        return null;
    }
}
