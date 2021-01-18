package com.github.kotooriiii.sorcery.spells.type.circle6;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class ForcePullSpell extends Spell {

    private static HashMap<UUID, Double> forcePullSpellCooldownMap = new HashMap<UUID, Double>();

    private final static int DEGREE_RANGE = 60;
    private final static int RADIUS = 12;

    public ForcePullSpell() {
        super(SpellType.FORCE_PULL,"Pulls everyone within " + RADIUS + " blocks of the direction you’re facing right into you. Sort of like a hook-in.", 6,  ChatColor.GOLD
                , new ItemStack[]{new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.STRING, 1), new ItemStack(Material.FEATHER, 1)}, 2.0f, 30, true, true, false);
    }

    @Override
    public void updateCooldown(Player player) {
        forcePullSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    forcePullSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                forcePullSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    @Override
    public boolean isCooldown(Player player) {
        if (forcePullSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = forcePullSpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(0, RoundingMode.UP);
            int value = bd.intValue();
            if (value == 0)
                value = 1;

            String time = "seconds";
            if (value == 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }

    @Override
    public boolean executeSpell(Player player) {

        for(Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), RADIUS, RADIUS, RADIUS))
        {
            if(entity.getType() != EntityType.PLAYER)
                continue;
            if(CitizensAPI.getNPCRegistry().isNPC(entity))
                continue;

            Vector normalizedDirection = entity.getLocation().toVector().clone().subtract(player.getLocation().toVector().clone()).normalize();

            if(Math.toDegrees(Math.acos(player.getLocation().getDirection().dot(normalizedDirection))) > DEGREE_RANGE)
                continue;

            Vector direction = player.getLocation().toVector().clone().subtract(entity.getLocation().toVector().clone());

            entity.setVelocity(normalizedDirection.multiply(direction));

            int timer = 0;
            int offset= 1;
            BlockIterator iterator = new BlockIterator(entity.getLocation().getWorld(), entity.getLocation().toVector(), direction, 1, 0);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(iterator.hasNext())
                    {
                        Block next = iterator.next();
                        entity.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, next.getLocation(), 10, 1,1,1);

                    } else {
                        this.cancel();
                    }

                }
            }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, timer*offset);



        }
        return true;
    }

}
