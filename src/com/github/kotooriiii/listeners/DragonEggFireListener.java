package com.github.kotooriiii.listeners;

import com.github.kotooriiii.sorcery.scrolls.Scroll;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.inventory.ItemStack;

public class DragonEggFireListener implements Listener {
    @EventHandler
    public void onPreventBurn(EntityCombustEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof Item)
        {
            Item item = (Item) entity;
            if(item.getItemStack().getType() == Material.DRAGON_EGG)
            {
                ItemStack itemStack = item.getItemStack();
                removeFire(item.getLocation());
                item.getWorld().dropItemNaturally(item.getLocation(), itemStack);
                item.remove();
            }
            else if(item.getItemStack().getType() == Scroll.scrollMaterial)
            {
                ItemStack itemStack = item.getItemStack();
                if(Scroll.isScroll(itemStack))
                {
                    removeFire(item.getLocation());
                    item.getWorld().dropItemNaturally(item.getLocation(), itemStack);
                    item.remove();
                }
            }
        }
    }

    private void removeFire(Location location)
    {

        Location duplicateLoc = location.clone();
        for(int x = -1; x < 1; x++)
        {
            for(int y = -1; y < 1; y++)
            {
                for(int z = -1; z < 1; z++)
                {
                    Location iterated = duplicateLoc.set(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                    if(iterated.getBlock().getType() == Material.FIRE)
                    {
                        iterated.getBlock().setType(Material.AIR);
                        iterated.getBlock().getRelative(BlockFace.DOWN).setType(Material.OBSIDIAN);
                    }
                }
            }
        }
    }
}
