package com.github.kotooriiii.skills.commands.blacksmithy;

import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class RepairCommand implements CommandExecutor {

    final int STAMINA_COST = 10;
    final int ADDED_XP = 25;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        //player
        if (!(sender instanceof Player))
            return false;

        //comand
        if (!cmd.getName().equalsIgnoreCase("repair"))
            return false;


        //Declare early variables
        final Player playerSender = (Player) sender;
        final UUID playerUUID = playerSender.getUniqueId();

        //Item in slot
        ItemStack mainHand = playerSender.getInventory().getItemInMainHand();
        ItemMeta meta = mainHand.getItemMeta();

        //Get ingredients for item
        ItemStack[] ingredients = getCost(mainHand);

        //If ingredients don't exist or the item isn't able to take any damage
        if (ingredients == null || !(meta instanceof Damageable)) {
            playerSender.sendMessage(ERROR_COLOR + "This item is not repairable.");
            return false;
        }

        //Inventory helper and construct error message
        InventoryUtil invHelper = new InventoryUtil(playerSender, ingredients, "to repair this item", 1, false);

        //If inventory doesn't have the necessary ingredients.
        if (!invHelper.hasIngredients())
            return false;

        //Make sure player has the stats necessary
        Stat stat = Stat.wrap(playerUUID);
        if (stat.getStamina() < STAMINA_COST) {
            playerSender.sendMessage(ERROR_COLOR + "You must have at least " + STAMINA_COST + " stamina to repair an item.");
            return false;
        }

        //Get the skill object
        SkillPlayer.Skill blacksmithy = SkillPlayer.wrap(playerUUID).getBlacksmithy();

        //Calculate chance
        int level = (int) blacksmithy.getLevel();
        double chance = level * (0.01);
        double random = Math.random();


        //If won!
        if (random < chance) {
            //Repair item
            ((Damageable) meta).setDamage(0);
            playerSender.sendMessage(ChatColor.GOLD + "You repair the item.");

        } else {

            //Damage item by third of durability

            int damageTaken = (int) Math.floor(mainHand.getType().getMaxDurability() / 3);

            if (((Damageable) meta).getDamage() - damageTaken < 0)
                playerSender.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            else
                ((Damageable) meta).setDamage(((Damageable) meta).getDamage() - damageTaken);

            playerSender.sendMessage(ChatColor.GRAY + "You failed to repair the item, it was damaged in the process.");
        }

        //Give xp for trying.
        blacksmithy.addXP(ADDED_XP);
        stat.setStamina(stat.getStamina() - STAMINA_COST);
        mainHand.setItemMeta(meta);
        invHelper.removeIngredients();
        playerSender.updateInventory();
        return true;
    }

    private ItemStack[] getCost(ItemStack itemStack) {
        Material material = itemStack.getType();

        switch (material) {

            //BOW
            case BOW:
                return new ItemStack[]{new ItemStack(Material.STRING, 1)};
            //DIAMOND
            case DIAMOND_AXE:
            case DIAMOND_SWORD:
            case DIAMOND_PICKAXE:
            case DIAMOND_HOE:
            case DIAMOND_SHOVEL:

            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:

            case DIAMOND_HORSE_ARMOR:
                return new ItemStack[]{new ItemStack(Material.DIAMOND, 1)};

            //GOLD
            case GOLDEN_AXE:
            case GOLDEN_SWORD:
            case GOLDEN_PICKAXE:
            case GOLDEN_HOE:
            case GOLDEN_SHOVEL:

            case GOLDEN_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:

            case GOLDEN_HORSE_ARMOR:
                return new ItemStack[]{new ItemStack(Material.GOLD_INGOT, 1)};

            //IRON
            case IRON_AXE:
            case IRON_SWORD:
            case IRON_PICKAXE:
            case IRON_HOE:
            case IRON_SHOVEL:

            case IRON_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:

            case IRON_HORSE_ARMOR:
                return new ItemStack[]{new ItemStack(Material.IRON_INGOT, 1)};

            //STONE
            case STONE_AXE:
            case STONE_SWORD:
            case STONE_PICKAXE:
            case STONE_HOE:
            case STONE_SHOVEL:

            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
                return new ItemStack[]{new ItemStack(Material.COBBLESTONE, 1)};

            case WOODEN_AXE:
            case WOODEN_SWORD:
            case WOODEN_PICKAXE:
            case WOODEN_HOE:
            case WOODEN_SHOVEL:
                return new ItemStack[]{new ItemStack(Material.OAK_PLANKS, 1), new ItemStack(Material.ACACIA_PLANKS, 1), new ItemStack(Material.BIRCH_PLANKS, 1), new ItemStack(Material.DARK_OAK_PLANKS, 1),
                        new ItemStack(Material.JUNGLE_PLANKS, 1), new ItemStack(Material.SPRUCE_PLANKS, 1)};

        }
        return null;
    }
}
