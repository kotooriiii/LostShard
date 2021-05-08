package com.github.kotooriiii.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TotemDropListener implements Listener {
    @EventHandler
    public void itemDrop(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getType() == EntityType.EVOKER) {
            List<ItemStack> drops = event.getDrops();
            ArrayList<ItemStack> newerDrop = new ArrayList<>(drops);
            newerDrop.removeIf(item -> item.getType() == Material.TOTEM_OF_UNDYING);

            drops.clear();
            for (ItemStack item : newerDrop)
                drops.add(item);
        }
    }
}
