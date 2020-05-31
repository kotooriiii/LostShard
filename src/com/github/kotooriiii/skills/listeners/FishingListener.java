package com.github.kotooriiii.skills.listeners;

import com.github.kotooriiii.skills.SkillPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FishingListener implements Listener {

    final int ADDED_XP = 50;

    @EventHandler
    public void fish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getCaught();

        if (entity == null)
            return;

        if (!(entity instanceof Item))
            return;

        if (!isFishingRod(player.getInventory().getItemInMainHand()))
            return;

        SkillPlayer.Skill fishingSkill = SkillPlayer.wrap(player.getUniqueId()).getFishing();

        ArrayList<ItemStack> rewards = getRewards(fishingSkill.getLevel());

        reward(player, entity, rewards);

        fishingSkill.addXP(ADDED_XP);
    }

    private boolean isFishingRod(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case FISHING_ROD:
                return true;
        }

        return false;
    }

    private void drop(Location location, Collection<ItemStack> ground, Vector velocity) {
        for (ItemStack itemStack : ground) {
            Entity entity = location.getWorld().dropItemNaturally(location, itemStack);
            entity.setVelocity(velocity);
        }
    }

    private void reward(Player player, Entity entity, ArrayList<ItemStack> rewards) {

        for (ItemStack rewardedItemStack : rewards) {

            Item rewardedItem = entity.getWorld().dropItem(entity.getLocation(), rewardedItemStack);
            Vector vector = player.getLocation().toVector().subtract(entity.getLocation().toVector());
            vector = vector.multiply(0.1);
            rewardedItem.setVelocity(vector);
        }
    }

    private ArrayList<ItemStack> getRewards(float level) {
        HashMap<ItemStack, Double> lootOfLevel = getLootOfLevel(level);
        ArrayList<ItemStack> rewards = new ArrayList<>();


        for (Map.Entry<ItemStack, Double> entry : lootOfLevel.entrySet()) {
            double random = Math.random();

            ItemStack item = entry.getKey();
            double chance = entry.getValue();

            if (random < chance) {
                rewards.add(item);
            }
        }

        return rewards;
    }

    private HashMap<ItemStack, Double> getLootOfLevel(float level) {
        HashMap<ItemStack, Double> lootTable = new HashMap<>();

        if (level >= 100) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.05);
            lootTable.put(new ItemStack(Material.TRIPWIRE_HOOK, 1), 0.05);
            lootTable.put(new ItemStack(Material.OBSIDIAN, 1), 0.05);
            lootTable.put(new ItemStack(Material.ROTTEN_FLESH, 1), 0.05);
            lootTable.put(new ItemStack(Material.FERMENTED_SPIDER_EYE, 1), 0.05);
            lootTable.put(new ItemStack(Material.JACK_O_LANTERN, 1), 0.05);
            lootTable.put(new ItemStack(Material.ANVIL, 1), 0.05);
            lootTable.put(new ItemStack(Material.DIAMOND, 1), 0.03);
            lootTable.put(new ItemStack(Material.BUCKET, 1), 0.01);
            lootTable.put(new ItemStack(Material.DIRT, 1), 0.01);
            lootTable.put(new ItemStack(Material.GOLD_BLOCK, 1), 0.01);
            lootTable.put(new ItemStack(Material.COBWEB, 1), 0.03);
            lootTable.put(new ItemStack(Material.DIAMOND_BLOCK, 1), 0.01);
            lootTable.put(new ItemStack(Material.MOOSHROOM_SPAWN_EGG, 1), 0.0025);
            lootTable.put(new ItemStack(Material.OAK_DOOR, 1), 0.02);
            lootTable.put(new ItemStack(Material.DRAGON_EGG, 1), 0.0075);
        } else if (90 <= level && level < 100) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.05);
            lootTable.put(new ItemStack(Material.TRIPWIRE_HOOK, 1), 0.05);
            lootTable.put(new ItemStack(Material.OBSIDIAN, 1), 0.05);
            lootTable.put(new ItemStack(Material.ROTTEN_FLESH, 1), 0.05);
            lootTable.put(new ItemStack(Material.FERMENTED_SPIDER_EYE, 1), 0.05);
            lootTable.put(new ItemStack(Material.JACK_O_LANTERN, 1), 0.05);
            lootTable.put(new ItemStack(Material.ANVIL, 1), 0.05);
            lootTable.put(new ItemStack(Material.DIAMOND, 1), 0.03);
            lootTable.put(new ItemStack(Material.BUCKET, 1), 0.01);
            lootTable.put(new ItemStack(Material.DIRT, 1), 0.01);
            lootTable.put(new ItemStack(Material.GOLD_BLOCK, 1), 0.01);
            lootTable.put(new ItemStack(Material.COBWEB, 1), 0.03);
            lootTable.put(new ItemStack(Material.DIAMOND_BLOCK, 1), 0.01);
            lootTable.put(new ItemStack(Material.MOOSHROOM_SPAWN_EGG, 1), 0.0025);
            lootTable.put(new ItemStack(Material.OAK_DOOR, 1), 0.02);
            lootTable.put(new ItemStack(Material.DRAGON_EGG, 1), 0.0075);
        } else if (80 <= level && level < 90) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.05);
            lootTable.put(new ItemStack(Material.GOLD_INGOT, 1), 0.05);
            lootTable.put(new ItemStack(Material.BOOKSHELF, 1), 0.05);
            lootTable.put(new ItemStack(Material.ROTTEN_FLESH, 1), 0.05);
            lootTable.put(new ItemStack(Material.GHAST_TEAR, 1), 0.05);
            lootTable.put(new ItemStack(Material.PUMPKIN, 1), 0.05);
            lootTable.put(new ItemStack(Material.ENCHANTING_TABLE, 1), 0.05);
            lootTable.put(new ItemStack(Material.DIAMOND_SWORD, 1), 0.03);
            lootTable.put(new ItemStack(Material.GOLD_BLOCK, 1), 0.02);
            lootTable.put(new ItemStack(Material.DIAMOND, 1), 0.02);
            lootTable.put(new ItemStack(Material.COBWEB, 1), 0.03);
            lootTable.put(new ItemStack(Material.DIAMOND_BLOCK, 1), 0.01);
            lootTable.put(new ItemStack(Material.MOOSHROOM_SPAWN_EGG, 1), 0.0025);
            lootTable.put(new ItemStack(Material.HONEY_BOTTLE, 1), 0.0075);

        } else if (70 <= level && level < 80) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.05);
            lootTable.put(new ItemStack(Material.GOLD_INGOT, 1), 0.05);
            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.05);
            lootTable.put(new ItemStack(Material.HAY_BLOCK, 1), 0.05);
            lootTable.put(new ItemStack(Material.HOPPER, 1), 0.05);
            lootTable.put(new ItemStack(Material.MELON, 1), 0.05);
            lootTable.put(new ItemStack(Material.ENCHANTING_TABLE, 1), 0.05);

            lootTable.put(new ItemStack(Material.DIAMOND, 1), 0.02);
            lootTable.put(new ItemStack(Material.EMERALD, 1), 0.03);
            lootTable.put(new ItemStack(Material.GOLD_BLOCK, 1), 0.02);
            lootTable.put(new ItemStack(Material.LAPIS_BLOCK, 1), 0.03);
            lootTable.put(new ItemStack(Material.IRON_BLOCK, 1), 0.03);
            lootTable.put(new ItemStack(Material.SLIME_BLOCK, 1), 0.02);
        } else if (60 <= level && level < 70) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.05);
            lootTable.put(new ItemStack(Material.IRON_INGOT, 1), 0.05);
            lootTable.put(new ItemStack(Material.GOLD_INGOT, 1), 0.1);
            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.05);
            lootTable.put(new ItemStack(Material.MELON_SEEDS, 1), 0.05);
            lootTable.put(new ItemStack(Material.CAKE, 1), 0.05);
            lootTable.put(new ItemStack(Material.MELON, 1), 0.05);
            lootTable.put(new ItemStack(Material.DIAMOND, 1), 0.02);
            lootTable.put(new ItemStack(Material.EMERALD, 1), 0.03);
            lootTable.put(new ItemStack(Material.GOLD_BLOCK, 1), 0.02);
            lootTable.put(new ItemStack(Material.LAPIS_BLOCK, 1), 0.03);
        } else if (50 <= level && level < 60) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.05);
            lootTable.put(new ItemStack(Material.IRON_INGOT, 1), 0.05);
            lootTable.put(new ItemStack(Material.GOLD_INGOT, 1), 0.1);
            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.05);
            lootTable.put(new ItemStack(Material.BOW, 1), 0.05);
            lootTable.put(new ItemStack(Material.FISHING_ROD, 1), 0.05);
            lootTable.put(new ItemStack(Material.REDSTONE, 1), 0.05);
            lootTable.put(new ItemStack(Material.MELON_SEEDS, 1), 0.05);
            lootTable.put(new ItemStack(Material.DIAMOND, 1), 0.02);
            lootTable.put(new ItemStack(Material.EMERALD, 1), 0.03);
        } else if (40 <= level && level < 50) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.05);
            lootTable.put(new ItemStack(Material.IRON_INGOT, 1), 0.05);
            lootTable.put(new ItemStack(Material.GOLD_INGOT, 1), 0.1);
            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.05);
            lootTable.put(new ItemStack(Material.COBBLESTONE, 1), 0.05);
            lootTable.put(new ItemStack(Material.SLIME_BALL, 1), 0.05);
            lootTable.put(new ItemStack(Material.EGG, 1), 0.05);
            lootTable.put(new ItemStack(Material.LEAD, 1), 0.05);

        } else if (30 <= level && level < 40) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.05);
            lootTable.put(new ItemStack(Material.IRON_INGOT, 1), 0.05);
            lootTable.put(new ItemStack(Material.GOLD_INGOT, 1), 0.1);
            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.05);
            lootTable.put(new ItemStack(Material.STICK, 1), 0.05);
            lootTable.put(new ItemStack(Material.LEAD, 1), 0.05);
            lootTable.put(new ItemStack(Material.SLIME_BALL, 1), 0.05);
        } else if (20 <= level && level < 30) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.05);
            lootTable.put(new ItemStack(Material.IRON_INGOT, 1), 0.1);
            lootTable.put(new ItemStack(Material.GOLD_INGOT, 1), 0.05);
            lootTable.put(new ItemStack(Material.MELON, 1), 0.05);
            lootTable.put(new ItemStack(Material.GOLDEN_SWORD, 1), 0.05);
            lootTable.put(new ItemStack(Material.BONE, 1), 0.05);
            lootTable.put(new ItemStack(Material.EMERALD, 1), 0.05);
        } else if (10 <= level && level < 20) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.05);
            lootTable.put(new ItemStack(Material.IRON_INGOT, 1), 0.05);
            lootTable.put(new ItemStack(Material.CAKE, 1), 0.05);
            lootTable.put(new ItemStack(Material.CLOCK, 1), 0.05);
            lootTable.put(new ItemStack(Material.MELON, 1), 0.10);
            lootTable.put(new ItemStack(Material.ROTTEN_FLESH, 1), 0.05);
        } else if (0 <= level && level < 10) {
            lootTable.put(new ItemStack(Material.FEATHER, 1), 0.10);
            lootTable.put(new ItemStack(Material.WHEAT_SEEDS, 1), 0.10);
            lootTable.put(new ItemStack(Material.COBBLESTONE, 1), 0.10);
        }
        return lootTable;
    }
}

