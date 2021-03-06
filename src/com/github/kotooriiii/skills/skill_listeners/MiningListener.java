package com.github.kotooriiii.skills.skill_listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.Skill;
import com.github.kotooriiii.skills.events.MiningSkillEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MiningListener implements Listener {

    final int ADDED_XP = 10;



    @EventHandler (priority = EventPriority.HIGH)
    public void mineBlock(BlockBreakEvent event) {

        if(event.isCancelled())
            return;

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!isStoneOrOre(block) || !hasPickaxe(player.getInventory().getItemInMainHand()))
            return;

        Skill miningSkill = LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getMining();

        ArrayList<ItemStack> rewards = getRewards(miningSkill.getLevel());

        MiningSkillEvent callingEvent = new MiningSkillEvent(player, rewards);
        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(callingEvent);

        if (callingEvent.isCancelled())
            return;

        drop(block.getLocation(), rewards);
        miningSkill.addXP(ADDED_XP);
    }

    private boolean hasPickaxe(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case DIAMOND_PICKAXE:
            case GOLDEN_PICKAXE:
            case IRON_PICKAXE:
            case STONE_PICKAXE:
            case WOODEN_PICKAXE:
                return true;
        }

        return false;
    }

    private boolean isStoneOrOre(Block block) {
        Material type = block.getType();
        switch (type) {
            case STONE:
            case ANDESITE:
            case DIORITE:
            case GRANITE:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case EMERALD_ORE:
            case REDSTONE_ORE:
            case LAPIS_ORE:
            case DIAMOND_ORE:
            case NETHER_QUARTZ_ORE:
                return true;
        }
        return false;
    }

    private void drop(Location location, Collection<ItemStack> ground) {
        for (ItemStack itemStack : ground)
            location.getWorld().dropItemNaturally(location, itemStack);
    }

    private Collection<ItemStack> reward(Player player, ArrayList<ItemStack> rewards) {

        HashMap<Integer, ItemStack> map = player.getInventory().addItem(rewards.toArray(new ItemStack[rewards.size()]));

        return map.values();
    }

    private ArrayList<ItemStack> getRewards(float level) {
        HashMap<ItemStack, Double> lootOfLevel = getLootOfLevel(level);
        ArrayList<ItemStack> rewards = new ArrayList<>();


        for (Map.Entry<ItemStack, Double> entry : lootOfLevel.entrySet()) {
            double random = Math.random();

            if(LostShardPlugin.isTutorial())
            {
                if(entry.getKey().getType() == Material.DIAMOND)
                    continue;
                random -= 0.1;
            }

            ItemStack item = entry.getKey();
            double chance = entry.getValue() * 10 / 2;

            if (random < chance) {
                rewards.add(item);
            }
        }
        return rewards;
    }

    private HashMap<ItemStack, Double> getLootOfLevel(float level) {
        /*
        0-10 10% chance of coal drop | 5% chance of coal ore drop

        10-20 5% chance of coal drop | 3% chance of coal ore drop | 5% chance of iron ore drop

        20-30 3% chance of coal drop | 2% chance of coal ore drop | 5% chance of iron ore drop | 2% chance of gold ore drop
        */

        HashMap<ItemStack, Double> lootTable = new HashMap<>();

        if (level >= 100) {
            lootTable.put(new ItemStack(Material.COAL_ORE, 1), 0.001);
            lootTable.put(new ItemStack(Material.COAL, 1), 0.001);

            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.008);

            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.003);

            lootTable.put(new ItemStack(Material.REDSTONE, 1), 0.001);
            lootTable.put(new ItemStack(Material.REDSTONE_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.LAPIS_LAZULI, 1), 0.001);
            lootTable.put(new ItemStack(Material.LAPIS_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.EMERALD_ORE, 1), 0.00035*2);
            lootTable.put(new ItemStack(Material.EMERALD, 2), 0.00035*2);

            lootTable.put(new ItemStack(Material.DIAMOND, 1), 0.00035);
            lootTable.put(new ItemStack(Material.DIAMOND_ORE, 1), 0.0004);
        } else if (90 <= level && level < 100) {
            lootTable.put(new ItemStack(Material.COAL_ORE, 1), 0.001);
            lootTable.put(new ItemStack(Material.COAL, 1), 0.001);

            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.008);

            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.003);

            lootTable.put(new ItemStack(Material.REDSTONE, 1), 0.001);
            lootTable.put(new ItemStack(Material.REDSTONE_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.LAPIS_LAZULI, 1), 0.001);
            lootTable.put(new ItemStack(Material.LAPIS_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.EMERALD_ORE, 1), 0.0002*2);
            lootTable.put(new ItemStack(Material.EMERALD, 2), 0.0002*2);

            lootTable.put(new ItemStack(Material.DIAMOND, 1), 0.0002);
            lootTable.put(new ItemStack(Material.DIAMOND_ORE, 1), 0.00025);
        } else if (80 <= level && level < 90) {
            lootTable.put(new ItemStack(Material.COAL_ORE, 1), 0.001);
            lootTable.put(new ItemStack(Material.COAL, 1), 0.001);

            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.008);

            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.002);

            lootTable.put(new ItemStack(Material.REDSTONE, 1), 0.001);
            lootTable.put(new ItemStack(Material.REDSTONE_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.LAPIS_LAZULI, 1), 0.001);
            lootTable.put(new ItemStack(Material.LAPIS_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.EMERALD_ORE, 1), 0.00015*2);
            lootTable.put(new ItemStack(Material.EMERALD, 2), 0.00015*2);

            lootTable.put(new ItemStack(Material.DIAMOND, 1), 0.00015);
            lootTable.put(new ItemStack(Material.DIAMOND_ORE, 1), 0.0002);

        } else if (70 <= level && level < 80) {
            lootTable.put(new ItemStack(Material.COAL_ORE, 1), 0.001);
            lootTable.put(new ItemStack(Material.COAL, 1), 0.001);

            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.008);

            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.002);

            lootTable.put(new ItemStack(Material.REDSTONE, 1), 0.002);
            lootTable.put(new ItemStack(Material.REDSTONE_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.LAPIS_LAZULI, 1), 0.001);
            lootTable.put(new ItemStack(Material.LAPIS_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.EMERALD_ORE, 1), 0.00015*2);
            lootTable.put(new ItemStack(Material.EMERALD, 2), 0.00015*2);

            lootTable.put(new ItemStack(Material.DIAMOND, 1), 0.00015);
        } else if (60 <= level && level < 70) {
            lootTable.put(new ItemStack(Material.COAL_ORE, 1), 0.001);
            lootTable.put(new ItemStack(Material.COAL, 1), 0.001);

            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.005);

            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.002);

            lootTable.put(new ItemStack(Material.REDSTONE, 1), 0.002);
            lootTable.put(new ItemStack(Material.REDSTONE_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.LAPIS_LAZULI, 1), 0.001);

            lootTable.put(new ItemStack(Material.EMERALD_ORE, 1), 0.00015*2);
            lootTable.put(new ItemStack(Material.EMERALD, 2), 0.00015*2);


        } else if (50 <= level && level < 60) {
            lootTable.put(new ItemStack(Material.COAL_ORE, 1), 0.001);
            lootTable.put(new ItemStack(Material.COAL, 1), 0.001);

            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.005);

            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.REDSTONE, 1), 0.002);
            lootTable.put(new ItemStack(Material.REDSTONE_ORE, 1), 0.001);

            lootTable.put(new ItemStack(Material.EMERALD_ORE, 1), 0.00015*2);
            lootTable.put(new ItemStack(Material.EMERALD, 1), 0.00015*2);

        } else if (40 <= level && level < 50) {
            lootTable.put(new ItemStack(Material.COAL_ORE, 1), 0.002);
            lootTable.put(new ItemStack(Material.COAL, 1), 0.002);

            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.005);

            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.002);

            lootTable.put(new ItemStack(Material.REDSTONE, 1), 0.002);

            lootTable.put(new ItemStack(Material.EMERALD_ORE, 1), 0.00015*2);
            lootTable.put(new ItemStack(Material.EMERALD, 1), 0.00015*2);

        } else if (30 <= level && level < 40) {
            lootTable.put(new ItemStack(Material.COAL_ORE, 1), 0.002);
            lootTable.put(new ItemStack(Material.COAL, 1), 0.002);

            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.005);

            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.003);

            lootTable.put(new ItemStack(Material.REDSTONE, 1), 0.002);
        } else if (20 <= level && level < 30) {
            lootTable.put(new ItemStack(Material.COAL_ORE, 1), 0.002);
            lootTable.put(new ItemStack(Material.COAL, 1), 0.003);

            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.005);

            lootTable.put(new ItemStack(Material.GOLD_ORE, 1), 0.002);
        } else if (10 <= level && level < 20) {
            lootTable.put(new ItemStack(Material.COAL, 1), 0.005);
            lootTable.put(new ItemStack(Material.IRON_ORE, 1), 0.003);
        } else if (0 <= level && level < 10) {
            lootTable.put(new ItemStack(Material.COAL, 1), 0.005);
        }
        return lootTable;
    }
}
