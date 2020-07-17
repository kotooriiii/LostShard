package com.github.kotooriiii.sorcery.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.awt.event.KeyEvent;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class NoMoreOldEnchantsListener implements Listener {
    @EventHandler
    public void onEnchant(InventoryOpenEvent event) {
        if (!event.getView().getType().equals(InventoryType.ENCHANTING))
            return;

        event.getPlayer().sendMessage(ERROR_COLOR + "You must use the Blacksmithy skill to enchant your items.");
        event.setCancelled(true);
    }

    @EventHandler
    public void onEnchant(InventoryMoveItemEvent event) {

        Inventory source = event.getSource();
        if (!source.getType().equals(InventoryType.ANVIL))
            return;

        if (!hasTwoItems(source))
            return;

        for (HumanEntity humanEntity : event.getInitiator().getViewers())
            humanEntity.sendMessage(ERROR_COLOR + "You must use the Blacksmithy skill to combine your items.");
        event.setCancelled(true);

    }

    private boolean hasTwoItems(Inventory inventory) {
        ItemStack item1 = inventory.getItem(0);
        ItemStack item2 = inventory.getItem(1);
        return item1 != null && item2 != null;
    }
}


