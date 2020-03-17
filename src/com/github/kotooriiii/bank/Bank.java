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
    private double currency=-1;
    public final static String NAME = ChatColor.GRAY + "Bank";
    private final static HashMap<UUID, Bank> banks = new HashMap<>();

    public Bank(UUID playerUUID, Inventory inventory, double currency)
    {
        this.inventory = inventory;
        this.playerUUID = playerUUID;
        this.currency=currency;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public double getCurrency()
    {
        return currency;
    }

    public void setCurrency(double currency) {
        this.currency = currency;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public static HashMap<UUID, Bank> getBanks() {
        return banks;
    }
}
