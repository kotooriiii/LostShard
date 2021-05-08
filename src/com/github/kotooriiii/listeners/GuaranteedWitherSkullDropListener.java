package com.github.kotooriiii.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuaranteedWitherSkullDropListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onSkullDrop(EntityDeathEvent event)
    {
        final LivingEntity entity = event.getEntity();
        if(entity.getType() != EntityType.WITHER_SKELETON)
            return;
        final List<ItemStack> drops = event.getDrops();

        boolean isDropped = false;

        for(ItemStack drop : drops)
        {
            if(drop.getType() == Material.WITHER_SKELETON_SKULL)
            {
                isDropped=true;
                break;
            }
        }

        if(!isDropped)
        {
            if(Math.random() < 0.5d)
            drops.add(new ItemStack(Material.WITHER_SKELETON_SKULL, 1));
        }

    }
}
