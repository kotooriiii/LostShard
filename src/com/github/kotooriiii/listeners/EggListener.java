package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class EggListener implements Listener {
    @EventHandler
    public void onEggDrop(EntityDropItemEvent event) {
        Item eggItem = event.getItemDrop();

        if (eggItem == null)
            return;

        if (!eggItem.getType().equals(EntityType.EGG))
            return;

        Entity entity = event.getEntity();

        if (!(event.getEntity() instanceof Chicken))
            return;

        Chicken chicken = (Chicken) entity;

        //is chicken and is egg ->

        final double INITIAL_TIME = 20 * 60 * 5;
        final double NEW_TIME = (double) (INITIAL_TIME / 2);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (chicken.isDead())
                    return;
                chicken.getLocation().getWorld().spawnEntity(chicken.getLocation(), eggItem.getType());
            }
        }.runTaskLater(LostShardPlugin.plugin, (long) NEW_TIME);


    }
}
