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

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class IgniteCommand implements CommandExecutor {

    final int STAMINA_COST = 15;
    final int ADDED_XP = 100;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        //player
        if (!(sender instanceof Player))
            return false;

        //comand
        if (!cmd.getName().equalsIgnoreCase("ignite"))
            return false;


        //Declare early variables
        final Player playerSender = (Player) sender;
        final UUID playerUUID = playerSender.getUniqueId();

        //Item in slot
        ItemStack mainHand = playerSender.getInventory().getItemInMainHand();
        ItemMeta meta = mainHand.getItemMeta();


        //If ingredients don't exist or the item isn't able to take any damage
        if (!isIgnitable(mainHand) || !(meta instanceof Damageable)) {
            playerSender.sendMessage(ERROR_COLOR + "This item is not able to be ignited.");
            return false;
        }

        Clan clan = LostShardPlugin.getClanManager().getClan(playerUUID);
        if (clan == null || !clan.hasIgniteTimer()) {
            playerSender.sendMessage(ERROR_COLOR + "You don't have the ignite buff.");
            return false;
        }


        //Get ingredients for item
        ItemStack[] ingredients = getCost(mainHand);

        //Inventory helper and construct error message
        InventoryUtil invHelper = new InventoryUtil(playerSender, ingredients, "to ignite this item", 1, false);

        //If inventory doesn't have the necessary ingredients.
        if (!invHelper.hasIngredients())
            return false;

        //Make sure player has the stats necessary
        Stat stat = Stat.wrap(playerUUID);
        if (stat.getStamina() < STAMINA_COST) {
            playerSender.sendMessage(ERROR_COLOR + "You must have at least " + STAMINA_COST + " stamina to ignite an item.");
            return false;
        }

        //Check if already maxed out
        if (!hasMoreEnchants(mainHand) || getBlacksmithyLevelNeeded(mainHand) == -1) {
            playerSender.sendMessage(ERROR_COLOR + "The item has reached the highest level to be ignited.");
            return false;
        }


        //Get the skill object
        Skill blacksmithy = LostShardPlugin.getSkillManager().getSkillPlayer(playerUUID).getActiveBuild().getBlacksmithy();

        //Calculate chance
        int level = (int) blacksmithy.getLevel();

        if (!hasBlacksmithyLevel(mainHand, level)) {
            playerSender.sendMessage(ERROR_COLOR + "You aren't high enough level to ignite this item.");
            return false;
        }

        BlacksmithySkillEvent event = new BlacksmithySkillEvent(playerSender, BlacksmithyType.IGNITE);
        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        //Harden
        if (!enchant(mainHand, playerSender)) {
            playerSender.sendMessage(ERROR_COLOR + "You cannot add conflicting enchantments.");
            return false;
        }
        playerSender.sendMessage(ChatColor.GOLD + "You ignite the item.");


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
        Material material = mainHand.getType();

        switch (material) {
            //DIAMOND
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
                //
            case GOLDEN_SWORD:

                //IRON
            case IRON_SWORD:

                //STONE
            case STONE_SWORD:

                //WOODEN
            case WOODEN_SWORD:
            case BOW:
                return 100;
        }

        return 0;
    }

    private boolean isIgnitable(ItemStack itemStack) {
        Material material = itemStack.getType();

        switch (material) {

            //DIAMOND
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
                //GOLD
            case GOLDEN_SWORD:
                //IRON
            case IRON_SWORD:
                //STONE
            case STONE_SWORD:
                //WOODEN
            case WOODEN_SWORD:
            case BOW:
                return true;
        }
        return false;
    }

    private boolean hasMoreEnchants(ItemStack itemStack) {

        int fireLevel;
        switch (itemStack.getType()) {
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
                //GOLD
            case GOLDEN_SWORD:
                //IRON
            case IRON_SWORD:
                //STONE
            case STONE_SWORD:
            case WOODEN_SWORD:
                fireLevel = itemStack.getEnchantmentLevel(Enchantment.FIRE_ASPECT);
                if (fireLevel < Enchantment.FIRE_ASPECT.getMaxLevel())
                    return true;
            case BOW:
                fireLevel = itemStack.getEnchantmentLevel(Enchantment.ARROW_FIRE);
                if (fireLevel < Enchantment.ARROW_DAMAGE.getMaxLevel())
                    return true;
        }
        return false;
    }

    private boolean enchant(ItemStack itemStack, Player player) {

        switch (itemStack.getType()) {
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
                //GOLD
            case GOLDEN_SWORD:
                //IRON
            case IRON_SWORD:
                //STONE
            case STONE_SWORD:
            case WOODEN_SWORD:

                if (!itemStack.getItemMeta().hasEnchant(Enchantment.FIRE_ASPECT) && itemStack.getItemMeta().hasConflictingEnchant(Enchantment.FIRE_ASPECT)) {
                    return false;
                }

                itemStack.removeEnchantment(Enchantment.FIRE_ASPECT);
                itemStack.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                break;
            case BOW:

                if (!itemStack.getItemMeta().hasEnchant(Enchantment.ARROW_FIRE) && itemStack.getItemMeta().hasConflictingEnchant(Enchantment.ARROW_FIRE))
                    return false;

                itemStack.removeEnchantment(Enchantment.ARROW_FIRE);
                itemStack.addEnchantment(Enchantment.ARROW_FIRE, 1);
                break;
        }
        Clan clan = LostShardPlugin.getClanManager().getClan(player.getUniqueId());

        clan.broadcast(ChatColor.YELLOW + player.getName() + STANDARD_COLOR + " has exhausted the ignite buff!");
        clan.setIgniteTimer(0);
        return true;
    }

    private ItemStack[] getCost(ItemStack itemStack) {

        Material material = itemStack.getType();
        int cost = 32;

        switch (material) {

            //DIAMOND
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
                //GOLD
            case GOLDEN_SWORD:
                //IRON
            case IRON_SWORD:
                //STONE
            case STONE_SWORD:
            case WOODEN_SWORD:
            case BOW:
                return new ItemStack[]{new ItemStack(Material.LAVA_BUCKET, 1), new ItemStack(Material.DIAMOND, cost)};

        }
        return null;
    }
}
