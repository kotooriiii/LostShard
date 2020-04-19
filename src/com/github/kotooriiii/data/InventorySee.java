package com.github.kotooriiii.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class InventorySee {

    private UUID inventoryOwner;

    public InventorySee(UUID inventoryOwnerUUID)
    {
        this.inventoryOwner = inventoryOwnerUUID;
    }

    public boolean invsee(Player invader)
    {
        if(!isOnline())
            return false;

        Player victim = Bukkit.getOfflinePlayer(inventoryOwner).getPlayer();
        PlayerInventory inventory = victim.getInventory();
        invader.openInventory(inventory);
        return true;
    }

    public boolean isOnline()
    {
        return Bukkit.getOfflinePlayer(this.inventoryOwner).isOnline();
    }
}
