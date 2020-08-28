package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerOpenChestListener implements Listener {
    @EventHandler
    public void onListen(InventoryOpenEvent event)
    {
        if(event.isCancelled())
            return;

        if(event.getInventory().getType() != InventoryType.CHEST)
            return;

        event.setCancelled(true);

        if(LostShardPlugin.getTutorialManager().wrap(event.getPlayer().getUniqueId()).getProgressionType() != TutorialProgressionType.GRAB_STICK_FROM_CHEST) {
            return;
        }

        Inventory inventory = Bukkit.createInventory(event.getPlayer(), 1, ChatColor.GRAY + "Stick Holder");
        inventory.addItem(new ItemStack(Material.STICK, 1));
        event.getPlayer().openInventory(inventory);
        LostShardPlugin.getTutorialManager().wrap(event.getPlayer().getUniqueId()).nextProgression(event.getPlayer().getLocation());
    }
}
