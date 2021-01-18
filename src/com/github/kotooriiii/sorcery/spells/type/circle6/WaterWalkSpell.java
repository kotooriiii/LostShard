package com.github.kotooriiii.sorcery.spells.type.circle6;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.listeners.WaterWalkListener;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class WaterWalkSpell extends Spell {

    private static HashMap<UUID, Double> fireWalkCooldownMap = new HashMap<UUID, Double>();
    private static final HashSet<UUID> waterWalkActiveSet = new HashSet<>();

    private final static int DURATION = 20;


    public WaterWalkSpell() {
        super(SpellType.WATER_WALK,
                "Allows you to run on water. Lasts for " + DURATION + " seconds.",
                6,
                ChatColor.RED,
                new ItemStack[]{new ItemStack(Material.LILY_PAD, 1), new ItemStack(Material.REDSTONE, 1)},
                2d,
                20,
                true, true, false);
    }

    @Override
    public boolean executeSpell(Player player) {

        final UUID uuid = player.getUniqueId();
        waterWalkActiveSet.add(uuid);


        new BukkitRunnable() {
            @Override
            public void run() {

                ArrayList<Block> blocks = WaterWalkListener.getBlocks().get(uuid);
                if (blocks != null) {
                    for (Block block : blocks)
                        player.sendBlockChange(block.getLocation(), block.getBlockData());
                }


                if (player != null) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_HIT, 5.0f, 2.333f);
                }
            }
        }.runTaskLater(LostShardPlugin.plugin, DURATION * 20);

        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        fireWalkCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    fireWalkCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                fireWalkCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (fireWalkCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = fireWalkCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(1, RoundingMode.HALF_UP);
            float value = bd.floatValue();
            if (value == 0)
                value = 0.1f;

            String time = "seconds";
            if (value <= 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }

    public static HashSet<UUID> getWaterWalkActiveSet() {
        return waterWalkActiveSet;
    }
}
