package com.github.kotooriiii.listeners;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ZombieDeathListener implements Listener {
    @EventHandler
    public void onZombieDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();
        if(!(entity instanceof Zombie))
            return;
        double random  = Math.random();
        final double chance = 0.75;

        if(!(random < chance))
            return;

        int randomInt = new Random().nextInt(5) + 1;

        entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.FEATHER, randomInt));


    }
}
