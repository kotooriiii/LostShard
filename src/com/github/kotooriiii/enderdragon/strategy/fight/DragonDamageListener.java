package com.github.kotooriiii.enderdragon.strategy.fight;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.enderdragon.EnderDragonManager;
import com.github.kotooriiii.enderdragon.entity.LSEnderDragon;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEnderDragonPart;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class DragonDamageListener implements Listener {

    @EventHandler
    public void onWitherHeal(EntityRegainHealthEvent event) {
        final Entity entity = event.getEntity();
        if (entity.getType() != EntityType.WITHER)
            return;
        if(entity.getWorld().getEnvironment() != World.Environment.THE_END)
            return;

        event.setCancelled(true);

    }

    @EventHandler
    public void onDamage(ProjectileHitEvent event) {
        if (event.getEntity().getWorld().getEnvironment() != World.Environment.THE_END)
            return;
        if (event.getHitEntity() == null || event.getHitEntity().getType() != EntityType.UNKNOWN)
            return;
        final EnderDragonManager enderDragonManager = LostShardPlugin.getEnderDragonManager();
        final LSEnderDragon lsEnderDragon = enderDragonManager.getSpawnStrategy().getLSEnderDragon();

        if (lsEnderDragon == null)
            return;


        if (!(event.getHitEntity() instanceof CraftEnderDragonPart))
            return;

        Entity damager = lsEnderDragon.getBukkitEntity();
        EnderDragon enderDragon = (EnderDragon) damager;

        if (event.getEntity() instanceof Wither || event.getEntity() instanceof WitherSkull) {

            final double newHealth = Math.min(enderDragon.getHealth() + 10, enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            final double oldRatio = enderDragon.getHealth() / enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            final double newRatio = newHealth / enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            enderDragon.setHealth(newHealth);

            if (newRatio >= 1) {
                LostShardPlugin.getEnderDragonManager().getFightStrategy().heal(oldRatio, newRatio);
            }


            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDragonDamage(EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ENDER_DRAGON)
            return;

        final EnderDragonManager enderDragonManager = LostShardPlugin.getEnderDragonManager();
        final LSEnderDragon lsEnderDragon = enderDragonManager.getSpawnStrategy().getLSEnderDragon();

        if (lsEnderDragon == null)
            return;

        final EnderDragon enderDragon = (EnderDragon) lsEnderDragon.getBukkitEntity();

        if (!entity.getUniqueId().equals(enderDragon.getUniqueId()))
            return;

        double newHealth = enderDragon.getHealth() - event.getFinalDamage();

        if (newHealth < 0)
            newHealth = 0;

        double oldRatio = (enderDragon.getHealth()) / enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double newRatio = (newHealth) / enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

        LostShardPlugin.getEnderDragonManager().getFightStrategy().damage(oldRatio, newRatio);


    }
}
