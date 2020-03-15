package com.github.kotooriiii.bank;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Bank {
    private UUID playerUUID;
    private Inventory inventory;
    public final static String NAME = ChatColor.GRAY + "Bank";
    private final static HashMap<UUID, Inventory> banks = new HashMap<>();

    public Bank(UUID playerUUID, Inventory inventory)
    {
        this.inventory = inventory;
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public static HashMap<UUID, Inventory> getBanks() {
        return banks;
    }
}
