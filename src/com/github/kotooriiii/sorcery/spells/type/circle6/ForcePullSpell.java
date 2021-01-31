package com.github.kotooriiii.sorcery.spells.type.circle6;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
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

    private ForcePullSpell() {
        super(SpellType.FORCE_PULL,"Pulls everyone within " + RADIUS + " blocks of the direction you’re facing right into you. Sort of like a hook-in.", 6,  ChatColor.GOLD
                , new ItemStack[]{new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.STRING, 1), new ItemStack(Material.FEATHER, 1)}, 2.0f, 30, true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.WITHER_SKELETON}, 0.02));

    }

    private  static ForcePullSpell instance;
    public static ForcePullSpell getInstance() {
        if (instance == null) {
            synchronized (ForcePushSpell.class) {
                if (instance == null)
                    instance = new ForcePullSpell();
            }
        }
        return instance;
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

        Clan clan = LostShardPlugin.getClanManager().getClan(player.getUniqueId());

        for(Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), RADIUS, RADIUS, RADIUS))
        {
            if(entity.getType() != EntityType.PLAYER)
                continue;
            if(CitizensAPI.getNPCRegistry().isNPC(entity))
                continue;
            if(entity == player)
                continue;
            if(clan != null)
            {
                if(clan.isInThisClan(entity.getUniqueId()) && clan.isFriendlyFire())
                    continue;
            }

            //Player to entity
            Vector normalizedDirection = entity.getLocation().toVector().clone().subtract(player.getLocation().toVector().clone());

            if (normalizedDirection.getX() == 0 && normalizedDirection.getY() == 0 && normalizedDirection.getZ() == 0)
                continue;


            normalizedDirection = normalizedDirection.normalize();

            if(Math.toDegrees(Math.acos(player.getLocation().getDirection().dot(normalizedDirection))) > DEGREE_RANGE)
                continue;

            //Entity to player
            Vector direction = player.getLocation().toVector().clone().subtract(entity.getLocation().toVector().clone());

            if(direction.getY() > 2)
            direction = direction.setY(2);

            entity.setVelocity(direction.multiply(0.35f));

            int timer = 0;
            int offset= 1;
            BlockIterator iterator = new BlockIterator(entity.getLocation().getWorld(), entity.getLocation().toVector(), direction, 1, 120);
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
