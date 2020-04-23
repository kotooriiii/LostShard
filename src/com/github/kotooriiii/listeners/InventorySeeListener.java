package com.github.kotooriiii.listeners;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.commands.InvseeCommand;
import com.github.kotooriiii.files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class InventorySeeListener implements Listener {
    @EventHandler
    public void inventorySeeManage(InventoryCloseEvent inventoryCloseEvent) {
        Inventory inventory = inventoryCloseEvent.getInventory();
        InventoryHolder inventoryHolder = inventory.getHolder();

        if (!(inventoryHolder instanceof Player))
            return;

        HumanEntity humanEntity = inventoryCloseEvent.getPlayer();
        if (!(humanEntity instanceof Player))
            return;

        Player inventoryOwnerPlayer = (Player) inventoryHolder;
        Player inventoryClosePlayer = (Player) humanEntity;

        if (!inventoryClosePlayer.hasPermission(STAFF_PERMISSION))
            return;

        if (inventoryOwnerPlayer.equals(inventoryClosePlayer))
            return;

        if (!inventoryCloseEvent.getView().getTitle().contains(InvseeCommand.IDENTIFIER))
            return;

        ItemStack[] itemStacks = new ItemStack[27];
        for (int i = 0; i < 27; i++) {
            itemStacks[i] = inventory.getItem(i);
        }
        inventoryOwnerPlayer.getInventory().setContents(itemStacks);

        PlayerInventory playerInventory = inventoryOwnerPlayer.getInventory();

        final int ADDER = 27 + 9;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    playerInventory.setBoots(inventory.getItem(i + ADDER));
                    break;
                case 1:
                    playerInventory.setLeggings(inventory.getItem(i + ADDER));
                    break;
                case 2:
                    playerInventory.setChestplate(inventory.getItem(i + ADDER));
                    break;
                case 3:
                    playerInventory.setHelmet(inventory.getItem(i + ADDER));
                    break;

            }
        }
    }
}
