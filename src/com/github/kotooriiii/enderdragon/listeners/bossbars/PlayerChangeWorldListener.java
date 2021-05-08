package com.github.kotooriiii.enderdragon.listeners.bossbars;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.enderdragon.EnderDragonManager;
import com.github.kotooriiii.enderdragon.entity.LSEnderDragon;
import com.github.kotooriiii.enderdragon.entity.LSWither;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class PlayerChangeWorldListener implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getEntity().getWorld().getEnvironment() != World.Environment.THE_END)
            return;
        if (event.getTarget() == null)
            return;
        if (event.getTarget().getType() == EntityType.PLAYER)
            return;

        if (event.getEntity().getType() == EntityType.WITHER) {

            final net.minecraft.server.v1_16_R3.Entity nmsEntity = ((CraftEntity) event.getEntity()).getHandle();
            if (nmsEntity instanceof LSWither) {
                LSWither lWither = (LSWither) nmsEntity;
                if (lWither.isHealingDragon()) {
                    if ((LostShardPlugin.getEnderDragonManager().getSpawnStrategy().getLSEnderDragon() == null))
                        lWither.setHealingDragon(false);
                    else
                        event.setTarget(LostShardPlugin.getEnderDragonManager().getSpawnStrategy().getLSEnderDragon().getBukkitEntity());
                    return;
                }

            }
        }


        final List<Player> players = event.getEntity().getWorld().getPlayers();

        double distance = Double.MAX_VALUE;
        Player targetPlayer = null;
        for (Player player : players) {

            if (player.getGameMode() != GameMode.SURVIVAL)
                continue;

            final double tempDist = player.getLocation().distance(event.getEntity().getLocation());

            if (tempDist < distance) {
                distance = tempDist;
                targetPlayer = player;
            }
        }

        if (targetPlayer == null) {
            event.setCancelled(true);
        } else {
            event.setTarget(targetPlayer);
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

        double ratio = (newHealth) / enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

        enderDragonManager.getBossBar().setProgress(ratio);

    }

    @EventHandler
    public void onDragonDamage(EntityRegainHealthEvent event) {
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

        double newHealth = enderDragon.getHealth() + event.getAmount();

        if (newHealth >= enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue())
            newHealth = enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

        double ratio = (newHealth) / enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

        enderDragonManager.getBossBar().setProgress(ratio);

    }



    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        switch (event.getPlayer().getWorld().getEnvironment()) {
            default:
                LostShardPlugin.getEnderDragonManager().getBossBar().removePlayer(event.getPlayer());
                return;
            case THE_END:
                LostShardPlugin.getEnderDragonManager().getBossBar().addPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        switch (event.getPlayer().getWorld().getEnvironment()) {
            case THE_END:
                LostShardPlugin.getEnderDragonManager().getBossBar().addPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        switch (event.getPlayer().getWorld().getEnvironment()) {
            case THE_END:
                LostShardPlugin.getEnderDragonManager().getBossBar().removePlayer(event.getPlayer());
        }
    }
}
