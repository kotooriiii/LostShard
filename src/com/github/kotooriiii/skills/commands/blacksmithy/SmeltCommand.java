package com.github.kotooriiii.skills.commands.blacksmithy;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.Skill;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.skills.events.BlacksmithySkillEvent;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class SmeltCommand implements CommandExecutor {

    final int STAMINA_COST = 15;
    final int ADDED_XP = 75;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        //player
        if (!(sender instanceof Player))
            return false;

        //comand
        if (!cmd.getName().equalsIgnoreCase("smelt"))
            return false;


        //Declare early variables
        final Player playerSender = (Player) sender;
        final UUID playerUUID = playerSender.getUniqueId();

        //Item in slot
        ItemStack mainHand = playerSender.getInventory().getItemInMainHand();
        ItemMeta meta = mainHand.getItemMeta();

        //If ingredients don't exist or the item isn't able to take any damage
        if (!isSmeltable(mainHand)) {
            playerSender.sendMessage(ERROR_COLOR + "This item is not smeltable.");
            return false;
        }

        //Make sure player has the stats necessary
        Stat stat = Stat.wrap(playerUUID);
        if (stat.getStamina() < STAMINA_COST) {
            playerSender.sendMessage(ERROR_COLOR + "You must have at least " + STAMINA_COST + " stamina to smelt an item.");
            return false;
        }

        //Get the skill object
        Skill blacksmithy = LostShardPlugin.getSkillManager().getSkillPlayer(playerUUID).getActiveBuild().getBlacksmithy();

        //Calculate chance
        int level = (int) blacksmithy.getLevel();
        double chance = getChanceOfSmelt(mainHand, level);

        BlacksmithySkillEvent event = new BlacksmithySkillEvent(playerSender, BlacksmithyType.SMELT);
        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        boolean inventoryFull = false;
        boolean failedToRecover = false;
        boolean atLeastOneSuccess = false;

        for (int i = 0; i < mainHand.getAmount(); i++) {
            double random = Math.random();

            //If won!
            if (random < chance) {

                ItemStack[] rewardItems = getRewards(mainHand, playerSender.getLocation());
                HashMap<Integer, ItemStack> items = playerSender.getInventory().addItem(rewardItems);

                if (!items.isEmpty()) {
                    inventoryFull = true;
                    for (ItemStack itemStack : items.values()) {
                        playerSender.getWorld().dropItemNaturally(playerSender.getLocation(), itemStack);
                    }
                }
                atLeastOneSuccess = true;


            } else {
                //Breaks item at the end
                failedToRecover = true;
            }

        }

        if (mainHand.getAmount() > 1) {
            if (inventoryFull)
                playerSender.sendMessage(STANDARD_COLOR + "Your inventory is full. Some of the resources have been dropped on the ground.");
            if (failedToRecover)
                playerSender.sendMessage(ChatColor.GRAY + "You smelted some items but failed to recover some materials.");
            if (atLeastOneSuccess) {
                playerSender.sendMessage(ChatColor.GOLD + "You smelt the items.");
            }
        } else {
            if (inventoryFull)
                playerSender.sendMessage(STANDARD_COLOR + "Your inventory is full. Some of the resources have been dropped on the ground.");
            if (failedToRecover)
                playerSender.sendMessage(ChatColor.GRAY + "You smelted the item but failed to recover any usable material.");
            if (atLeastOneSuccess) {
                playerSender.sendMessage(ChatColor.GOLD + "You smelt the item.");
            }
        }


//        playerSender.playSound(playerSender.getLocation());

        //Give xp for trying.
        stat.setStamina(stat.getStamina() - STAMINA_COST);
        if(mainHand.getType().name().toLowerCase().startsWith("stone_") && level > 50)
        {

        }
        else if (mainHand.getType().name().toLowerCase().startsWith("iron_")  && level > 75)
        {

        } else {
            blacksmithy.addXP(getXP(mainHand));
        }
        mainHand.setItemMeta(meta);
        playerSender.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        playerSender.updateInventory();

        return true;
    }

    private int getXP(ItemStack itemStack) {
        Material material = itemStack.getType();

        switch (material) {

            //NETHERITE
            case NETHERITE_AXE:
            case NETHERITE_SWORD:
            case NETHERITE_PICKAXE:
            case NETHERITE_HOE:
            case NETHERITE_SHOVEL:

            case NETHERITE_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:

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

            case GOLDEN_APPLE:
                return 100;

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
                return 75;

            //STONE & WOOD
            case STONE_AXE:
            case STONE_SWORD:
            case STONE_PICKAXE:
            case STONE_HOE:
            case STONE_SHOVEL:

                //BOW
            case BOW:


            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:

            case WOODEN_AXE:
            case WOODEN_SWORD:
            case WOODEN_PICKAXE:
            case WOODEN_HOE:
            case WOODEN_SHOVEL:
                return 50;

        }
        return -1;
    }

    private double getChanceOfSmelt(ItemStack itemStack, int level) {

        Material material = itemStack.getType();

        double useLevel = 0;
        if (0 <= level && level <= 30) {
            useLevel = level - 0;

        } else if (30 <= level && level <= 60) {
            useLevel = level - 30;
        } else if (60 <= level && level <= 80) {
            useLevel = level - 60;

        } else if (80 <= level && level <= 100) {
            useLevel = level - 80;
        }


        switch (material) {


            //NETHERITE
            case NETHERITE_AXE:
            case NETHERITE_SWORD:
            case NETHERITE_PICKAXE:
            case NETHERITE_HOE:
            case NETHERITE_SHOVEL:

            case NETHERITE_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
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
                if (level < 80)
                    return 0;
                else if (level > 100)
                    return 1;
                else
                    return ((double) 1 / 20) * (useLevel);
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

            case GOLDEN_APPLE:

                if (level < 60)
                    return 0;
                else if (level > 80)
                    return 1;
                else
                    return ((double) 1 / 20) * (useLevel);
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
                if (level < 30)
                    return 0;
                else if (level > 60)
                    return 1;
                else
                    return ((double) 1 / 30) * (useLevel);
                //STONE & WOOD
            case STONE_AXE:
            case STONE_SWORD:
            case STONE_PICKAXE:
            case STONE_HOE:
            case STONE_SHOVEL:

                //BOW
            case BOW:
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:

            case WOODEN_AXE:
            case WOODEN_SWORD:
            case WOODEN_PICKAXE:
            case WOODEN_HOE:
            case WOODEN_SHOVEL:
                if (level < 0)
                    return 0;
                else if (level > 30)
                    return 1;
                else
                    return ((double) 1 / 30) * (useLevel);
        }
        return -1;
    }

    private ItemStack[] getRewards(ItemStack itemStack, Location location) {
        Material material = itemStack.getType();

        switch (material) {

            //BOW
            case BOW:
                return new ItemStack[]{new ItemStack(Material.STRING, 1)};

            //DIAMOND
            case NETHERITE_AXE:
            case NETHERITE_SWORD:
            case NETHERITE_PICKAXE:
            case NETHERITE_HOE:
            case NETHERITE_SHOVEL:
                return new ItemStack[]{new ItemStack(Material.NETHERITE_INGOT, 1)};

            case NETHERITE_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
                return new ItemStack[]{new ItemStack(Material.NETHERITE_INGOT, 3)};
            //DIAMOND
            case DIAMOND_AXE:
            case DIAMOND_SWORD:
            case DIAMOND_PICKAXE:
            case DIAMOND_HOE:
            case DIAMOND_SHOVEL:
                return new ItemStack[]{new ItemStack(Material.DIAMOND, 1)};

            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_HORSE_ARMOR:
                return new ItemStack[]{new ItemStack(Material.DIAMOND, 3)};

            //GOLD APPLE
            case GOLDEN_APPLE:
                return new ItemStack[]{new ItemStack(Material.GOLD_INGOT, 5)};
            //GOLD
            case GOLDEN_AXE:
            case GOLDEN_SWORD:
            case GOLDEN_PICKAXE:
            case GOLDEN_HOE:
            case GOLDEN_SHOVEL:
                return new ItemStack[]{new ItemStack(Material.GOLD_INGOT, 1)};

            case GOLDEN_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_HORSE_ARMOR:
                return new ItemStack[]{new ItemStack(Material.GOLD_INGOT, 3)};

            //IRON
            case IRON_AXE:
            case IRON_SWORD:
            case IRON_PICKAXE:
            case IRON_HOE:
            case IRON_SHOVEL:
                return new ItemStack[]{new ItemStack(Material.IRON_INGOT, 1)};

            case IRON_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_HORSE_ARMOR:
                return new ItemStack[]{new ItemStack(Material.IRON_INGOT, 3)};

            //STONE
            case STONE_AXE:
            case STONE_SWORD:
            case STONE_PICKAXE:
            case STONE_HOE:
            case STONE_SHOVEL:
                return new ItemStack[]{new ItemStack(Material.COBBLESTONE, 1)};

            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
                return new ItemStack[]{new ItemStack(Material.COBBLESTONE, 3)};

            case WOODEN_AXE:
            case WOODEN_SWORD:
            case WOODEN_PICKAXE:
            case WOODEN_HOE:
            case WOODEN_SHOVEL:
                return new ItemStack[]{getBiomeRespectiveWood(location)};

        }
        return null;
    }

    private boolean isSmeltable(ItemStack itemStack) {
        Material material = itemStack.getType();

        switch (material) {

            //BOW
            case BOW:

            case NETHERITE_AXE:
            case NETHERITE_SWORD:
            case NETHERITE_PICKAXE:
            case NETHERITE_HOE:
            case NETHERITE_SHOVEL:

            case NETHERITE_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
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

            case GOLDEN_APPLE:

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

            case WOODEN_AXE:
            case WOODEN_SWORD:
            case WOODEN_PICKAXE:
            case WOODEN_HOE:
            case WOODEN_SHOVEL:
                return true;

        }
        return false;
    }

    private ItemStack getBiomeRespectiveWood(Location location) {
        Biome biome = location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        switch (biome) {
            default:
            case PLAINS:
            case BEACH:
            case OCEAN:
            case RIVER:
            case DESERT:
            case MOUNTAINS:
            case SWAMP:
            case ERODED_BADLANDS:
            case MODIFIED_WOODED_BADLANDS_PLATEAU:
            case MODIFIED_BADLANDS_PLATEAU:
            case FOREST:
            case NETHER_WASTES:
            case WOODED_HILLS:
            case BASALT_DELTAS:
            case WARPED_FOREST:
            case CRIMSON_FOREST:
            case SOUL_SAND_VALLEY:
            case THE_END:
            case DEEP_OCEAN:
            case MUSHROOM_FIELDS:
            case MUSHROOM_FIELD_SHORE:
            case DESERT_HILLS:
            case WOODED_MOUNTAINS:
            case BADLANDS:
            case BADLANDS_PLATEAU:
            case WOODED_BADLANDS_PLATEAU:
            case SMALL_END_ISLANDS:
            case DEEP_WARM_OCEAN:
            case DEEP_LUKEWARM_OCEAN:
            case END_MIDLANDS:
            case END_HIGHLANDS:
            case END_BARRENS:
            case WARM_OCEAN:
            case LUKEWARM_OCEAN:
            case THE_VOID:
            case SUNFLOWER_PLAINS:
            case DESERT_LAKES:
            case FLOWER_FOREST:
            case SWAMP_HILLS:
                return new ItemStack(Material.OAK_PLANKS, 1);
            case TAIGA:
            case SNOWY_TUNDRA:
            case SNOWY_MOUNTAINS:
            case FROZEN_OCEAN:
            case FROZEN_RIVER:
            case TAIGA_HILLS:
            case SNOWY_BEACH:
            case MOUNTAIN_EDGE:
            case SNOWY_TAIGA:
            case SNOWY_TAIGA_HILLS:
            case GIANT_TREE_TAIGA:
            case GIANT_TREE_TAIGA_HILLS:
            case COLD_OCEAN:
            case DEEP_COLD_OCEAN:
            case DEEP_FROZEN_OCEAN:
            case TAIGA_MOUNTAINS:
            case GRAVELLY_MOUNTAINS:
            case GIANT_SPRUCE_TAIGA:
            case GIANT_SPRUCE_TAIGA_HILLS:
            case MODIFIED_GRAVELLY_MOUNTAINS:
            case STONE_SHORE:
            case ICE_SPIKES:
            case SNOWY_TAIGA_MOUNTAINS:

                return new ItemStack(Material.SPRUCE_PLANKS, 1);
            case JUNGLE:
            case JUNGLE_HILLS:
            case JUNGLE_EDGE:
            case MODIFIED_JUNGLE:
            case MODIFIED_JUNGLE_EDGE:
            case BAMBOO_JUNGLE:
            case BAMBOO_JUNGLE_HILLS:

                return new ItemStack(Material.JUNGLE_PLANKS, 1);
            case BIRCH_FOREST:
            case BIRCH_FOREST_HILLS:
            case TALL_BIRCH_FOREST:
            case TALL_BIRCH_HILLS:
                return new ItemStack(Material.BIRCH_PLANKS, 1);
            case DARK_FOREST:
            case DARK_FOREST_HILLS:
                return new ItemStack(Material.DARK_OAK_PLANKS, 1);
            case SAVANNA:
            case SAVANNA_PLATEAU:
            case SHATTERED_SAVANNA:
            case SHATTERED_SAVANNA_PLATEAU:
                return new ItemStack(Material.ACACIA_PLANKS, 1);

        }
    }
}
