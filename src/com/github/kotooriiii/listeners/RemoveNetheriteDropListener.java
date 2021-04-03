package com.github.kotooriiii.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.Iterator;
import java.util.List;

public class RemoveNetheriteDropListener implements Listener {
    @EventHandler
    public void dropNetherite(BlockDropItemEvent event) {
        final List<Item> items = event.getItems();
        final Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getItemStack().getType() == Material.ANCIENT_DEBRIS) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void dropNetherite(InventoryOpenEvent event) {
        final Inventory inventory = event.getInventory();
        inventory.remove(Material.ANCIENT_DEBRIS);

    }


}
